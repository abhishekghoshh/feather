package io.github.abhishekghoshh.crud.repository;

import io.github.abhishekghoshh.crud.model.Blog;
import io.github.abhishekghoshh.crud.model.User;
import io.github.abhishekghoshh.crud.model.UserWroteBlogsRelation;
import io.github.abhishekghoshh.crud.neo4j.Neo4jRepository;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeAlreadyExistingException;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.exception.CypherException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        try {
            long id = neo4jRepository.create(user);
            logger.info("user id is {}", id);
            return user;
        } catch (CypherException ex) {
            logger.error("User saving : exception message {}", ex.getMessage());
            if ("Neo.ClientError.Schema.ConstraintValidationFailed".equals(ex.getCode())) {
                throw new NodeAlreadyExistingException("email id already present");
            }
            throw new NodeAlreadyExistingException(ex.getMessage());
        }
    }

    public User updateUser(User user) throws NodeNotFoundException, NodeAlreadyExistingException {
        try {
            return neo4jRepository.updateById(user);
        } catch (CypherException ex) {
            logger.error("User updating : exception message {}", ex.getMessage());
            if ("Neo.ClientError.Schema.ConstraintValidationFailed".equals(ex.getCode())) {
                throw new NodeAlreadyExistingException("email id already present");
            }
            throw new NodeAlreadyExistingException(ex.getMessage());
        }

    }

    public void deleteUser(Long userId) throws NodeNotFoundException {
        User user = neo4jRepository.findById(User.class, userId);
        String query = "MATCH (u:User {email: $email}) OPTIONAL MATCH (u)-[:WROTE]->(b:Blog) DETACH DELETE u, b";
        Map<String, Object> params = Map.of("email", user.getEmail());
        neo4jRepository.withTransaction(session -> session.query(query, params));
    }

    public User getUserWithBlogs(Long userId) throws NodeNotFoundException {
        User user = neo4jRepository.findById(User.class, userId);
        String cypher = "MATCH (u:User {email: $email})-[:WROTE]->(b:Blog) RETURN b";
        Map<String, Object> params = Map.of("email", user.getEmail());
        Iterable<Blog> blogs = neo4jRepository.withSession(session -> session.query(Blog.class, cypher, params));
        List<Blog> userBlogs = new ArrayList<>();
        blogs.forEach(userBlogs::add);
        user.setBlogs(userBlogs);
        return user;
    }

    public Blog addBlogsToUser(Long userId, Blog blog) throws NodeNotFoundException {
        User user = neo4jRepository.findById(User.class, userId);
        neo4jRepository.create(blog);
        UserWroteBlogsRelation relationshipNode = new UserWroteBlogsRelation(user, blog);
        neo4jRepository.create(relationshipNode);
        return blog;
    }

    public Blog getBlogForUser(User user, Long blogId) throws NodeNotFoundException {
        String cypher = "MATCH (u:User {email: $email})-[r:WROTE {uid: $uid}]->(b:Blog) RETURN u, r, b";
        Map<String, Object> params = Map.of(
                "email", user.getEmail(),
                "uid", blogId
        );
        UserWroteBlogsRelation relation = neo4jRepository.withSession(session ->
                session.queryForObject(UserWroteBlogsRelation.class, cypher, params)
        );
        if (null == relation) {
            throw new NodeNotFoundException("Blog not found for user with id " + blogId);
        }
        return (Blog) relation.getEnd();
    }

    public void updateBlog(Blog blog) {
        neo4jRepository.update(blog);
    }
    public void deleteBlog(Blog blog) {
        neo4jRepository.delete(blog);
    }
}
