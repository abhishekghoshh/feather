package io.github.abhishekghoshh.crud.neo4j;

import ac.simons.neo4j.migrations.core.Migrations;
import ac.simons.neo4j.migrations.core.MigrationsConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

import java.util.function.Function;

public abstract class Neo4jConnector implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(Neo4jConnector.class);

    private final String url;
    private final String username;
    private final String password;
    private final String database;
    private final String packageName;
    private boolean applyMigration = false;


    private BoltDriver driver;
    private SessionFactory sessionFactory;

    public Neo4jConnector(String url, String username, String password, String database, String packageName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.database = database;
        this.packageName = packageName;
        build();
    }

    public Neo4jConnector applyMigrations() {
        this.applyMigration = true;
        return this;
    }

    private void startMigrations(String url, String username, String password, String database, String schemaDatabase) {
        var graphDatabaseDriver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
        MigrationsConfig migrationConfig = MigrationsConfig.builder()
                .withLocationsToScan("classpath:db/migrations")
                .withDatabase(database)
                .withSchemaDatabase(schemaDatabase)
                .build();
        var migrations = new Migrations(migrationConfig, graphDatabaseDriver);

        migrations.apply(true).ifPresent(migrationVersion ->
                logger.info("migration version {}", migrationVersion.getValue())
        );
    }

    // Run after the bean has been created
    protected Neo4jConnector build() {
        this.driver = buildDriver(url, username, password, database);
        this.sessionFactory = buildSessionFactory(this.driver, packageName);
        logger.debug("migrations flag: {}", this.applyMigration);
        if (this.applyMigration) {
            logger.debug("Applying neo4j migrations");
            startMigrations(url, username, password, database, database);
        }
        return this;
    }

    private BoltDriver buildDriver(String url, String username, String password, String database) {
        BoltDriver driver = new BoltDriver();
        Configuration configuration = new Configuration.Builder()
                .uri(url)
                .verifyConnection(true)
                .withCustomProperty(BoltDriver.CONFIG_PARAMETER_BOLT_LOGGING, Logging.slf4j())
                .credentials(username, password)
                .database(database)
                .build();
        driver.configure(configuration);
        return driver;
    }

    private SessionFactory buildSessionFactory(BoltDriver driver, String packageName) {
        return new SessionFactory(driver, packageName);
    }

    private Session getSession() {
        return this.sessionFactory.openSession();
    }

    public <R> R withTransaction(Function<Session, R> function) {
        Session session = getSession();
        try (Transaction transaction = session.beginTransaction(Transaction.Type.READ_WRITE)) {
            R result = function.apply(session);
            transaction.commit();
            return result;
        }
    }

    public <R> R withSession(Function<Session, R> function) {
        return function.apply(getSession());
    }

    @Override
    public void close() {
        if (driver != null) {
            driver.close();
        }
    }
}
