package io.github.abhishekghoshh.crud.neo4j;


import io.github.abhishekghoshh.crud.neo4j.exception.NodeNotFoundException;
import io.github.abhishekghoshh.crud.neo4j.node.Neo4jNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Neo4jRepository extends Neo4jConnector implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(Neo4jRepository.class);
    private SimpleDateFormat dateFormatter;

    public Neo4jRepository(String url, String username, String password, String database, String packageName) {
        super(url, username, password, database, packageName);
    }

    public Neo4jRepository applyMigrations() {
        super.applyMigrations();
        return this;
    }


    @Override
    public Neo4jRepository build() {
        super.build();
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ");
        return this;
    }

    private String formattedDate(Date date) {
        return dateFormatter.format(date);
    }

    private Date currentTime() {
        return new Date();
    }

    public <T extends Neo4jNode> List<T> findAll(Class<T> className) {
        return withSession(session -> {
            Iterable<T> iterable = session.loadAll(className);
            List<T> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list;
        });
    }

    public <T extends Neo4jNode> T findById(Class<T> className, long id) throws NodeNotFoundException {
        Optional<T> optionalNode = withSession(session ->
                Optional.ofNullable(session.load(className, id))
        );
        if (optionalNode.isEmpty())
            throw new NodeNotFoundException("Entity " + className.getName() + " not found " + id);
        return optionalNode.get();
    }

    public <T extends Neo4jNode> T updateById(T data) throws NodeNotFoundException {
        T existingData = findById((Class<T>) data.getClass(), data.getId());
        return withTransaction(session -> {
            data.setCreationTimeStamp(existingData.getCreationTimeStamp());
            data.setLastUpdateTimeStamp(formattedDate(currentTime()));
            session.save(data);
            return data;
        });
    }

    public <T extends Neo4jNode> T update(T data) {
        return withTransaction(session -> {
            data.setLastUpdateTimeStamp(formattedDate(currentTime()));
            session.save(data);
            return data;
        });
    }

    public <T extends Neo4jNode> long create(T data) {
        return withTransaction(session -> {
            data.setId(null);
            String time = formattedDate(currentTime());
            data.setCreationTimeStamp(time);
            data.setLastUpdateTimeStamp(time);
            session.save(data);
            return data.getId();
        });
    }

    public <T extends Neo4jNode> Optional<T> findOne(Class<T> className, Filter filter) {
        List<T> result = findAll(className, filter);
        return result.isEmpty() ?
                Optional.empty() :
                Optional.of(result.getFirst());
    }

    public <T extends Neo4jNode> List<T> findAll(Class<T> className, Filter filter) {
        return withSession(session -> {
            Iterable<T> result = session.loadAll(className, filter);
            List<T> list = new ArrayList<>();
            result.forEach(list::add);
            return list;
        });
    }

    public <T extends Neo4jNode> Optional<T> findOne(Class<T> className, Filters filters) {
        List<T> result = findAll(className, filters);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public <T extends Neo4jNode> List<T> findAll(Class<T> className, Filters filters) {
        return withSession(session -> {
            Iterable<T> result = session.loadAll(className, filters);
            List<T> list = new ArrayList<>();
            result.forEach(list::add);
            return list;
        });
    }

    public <T extends Neo4jNode> void deleteById(Class<T> className, long id) throws NodeNotFoundException {
        T data = findById(className, id);
        delete(data);
    }

    public <T extends Neo4jNode> void delete(T data) {
        withTransaction(session -> {
            session.delete(data);
            return true;
        });
    }

}
