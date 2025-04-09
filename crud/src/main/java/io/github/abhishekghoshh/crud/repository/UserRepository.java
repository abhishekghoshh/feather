package io.github.abhishekghoshh.crud.repository;

import io.github.abhishekghoshh.crud.model.User;
import io.github.abhishekghoshh.crud.neo4j.Neo4jRepository;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeAlreadyExistingException;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    private final Neo4jRepository neo4jRepository;

    public UserRepository(Neo4jRepository neo4jRepository) {
        this.neo4jRepository = neo4jRepository;
    }

    public List<User> getUsers() {
        return neo4jRepository.findAll(User.class);
    }

    public User getUserById(Long userId) throws NodeNotFoundException {
        return neo4jRepository.findById(User.class, userId);
    }

    public User saveUser(User user) throws NodeAlreadyExistingException {
        if (findUserByEmail(user).isPresent())
            throw new NodeAlreadyExistingException("email id already present");
        long id = neo4jRepository.create(user);
        logger.info("user id is {}", id);
        return user;
    }

    public Optional<User> findUserByEmail(User user) {
        Filter filter = new Filter("email", ComparisonOperator.EQUALS, user.getEmail());
        return neo4jRepository.findOne(User.class, filter);
    }

    public User updateUser(User user) throws NodeNotFoundException {
        return neo4jRepository.update(user);
    }

    public void deleteUser(Long userId) throws NodeNotFoundException {
        neo4jRepository.deleteById(User.class, userId);
    }
}
