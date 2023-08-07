package com.github.neo4j.repository

import org.neo4j.driver.Logging
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver.CONFIG_PARAMETER_BOLT_LOGGING
import org.neo4j.ogm.session.{Neo4jSession, Session, SessionFactory}
import org.neo4j.ogm.transaction.Transaction
import org.slf4j.{Logger, LoggerFactory}

import java.text.SimpleDateFormat
import java.util.Date

abstract class Neo4jConnector(
    url: String,
    username: String,
    password: String,
    database: String,
    packageName: String)
    extends AutoCloseable:

  private var _logger: Logger                = _
  private var driver: BoltDriver             = _
  private var sessionFactory: SessionFactory = _

  protected def build(): Neo4jConnector =
    this.driver = buildDriver(url, username, password, database)
    this.sessionFactory = buildSessionFactory(this.driver, packageName)
    this._logger = _buildLogger
    this

  private def _buildLogger: Logger = LoggerFactory.getLogger(getClass)

  private def buildDriver(url: String, username: String, password: String, database: String)
      : BoltDriver =
    val driver = new BoltDriver
    val configuration = new Configuration.Builder()
      .uri(url)
      .verifyConnection(true)
      .withCustomProperty(CONFIG_PARAMETER_BOLT_LOGGING, Logging.slf4j())
      .credentials(username, password)
      .database(database)
      .build
    driver.configure(configuration)
    driver

  private def buildSessionFactory(driver: BoltDriver, packageNames: String): SessionFactory =
    new SessionFactory(driver, packageNames)

  private def session: Session = this.sessionFactory.openSession.asInstanceOf[Neo4jSession]

  protected def withTransaction[R](function: Function[Session, R]): R =
    val session     = this.session
    val transaction = session.beginTransaction(Transaction.Type.READ_WRITE)
    val results     = function(session)
    transaction.commit()
    results

  protected def withSession[R](function: Function[Session, R]): R = function(this.session)

  override def close(): Unit = driver.close()
end Neo4jConnector
