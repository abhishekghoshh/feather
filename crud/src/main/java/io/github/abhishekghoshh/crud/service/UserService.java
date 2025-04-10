package io.github.abhishekghoshh.crud.service;

import io.github.abhishekghoshh.crud.dto.ResourceType;
import io.github.abhishekghoshh.crud.exception.BlogNotFoundException;
import io.github.abhishekghoshh.crud.exception.DuplicateResourceException;
import io.github.abhishekghoshh.crud.exception.ResourceException;
import io.github.abhishekghoshh.crud.exception.UserNotFoundException;
import io.github.abhishekghoshh.crud.model.Blog;
import io.github.abhishekghoshh.crud.model.User;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeAlreadyExistingException;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeNotFoundException;
import io.github.abhishekghoshh.crud.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUserById(Long userId) throws UserNotFoundException {
        try {
            return userRepository.getUserById(userId);
        } catch (NodeNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
    }

    public User saveUser(User user) throws DuplicateResourceException {
        try {
            return userRepository.saveUser(user);
        } catch (NodeAlreadyExistingException e) {
            throw new DuplicateResourceException(ResourceType.EMAIL, e.getMessage());
        }
    }

    public User updateUser(Long userId, User user) throws NodeNotFoundException, DuplicateResourceException {
        try {
            user.setId(userId);
            return userRepository.updateUser(user);
        } catch (NodeAlreadyExistingException e) {
            throw new DuplicateResourceException(ResourceType.EMAIL, e.getMessage());
        }
    }

    public void deleteUser(Long userId) throws UserNotFoundException {
        try {
            userRepository.deleteUser(userId);
        } catch (NodeNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
    }

    public User getUserWithBlogs(Long userId) throws UserNotFoundException {
        try {
            return userRepository.getUserWithBlogs(userId);
        } catch (NodeNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
    }

    public Blog getBlogOfUser(Long userId, Long blogId) throws ResourceException {
        User user = getUserById(userId);
        logger.info("user is {}", user);
        try {
            return userRepository.getBlogForUser(user, blogId);
        } catch (NodeNotFoundException e) {
            logger.error(e);
            throw new BlogNotFoundException(blogId);
        }
    }

    public Blog addBlogsToUser(Long userId, Blog blog) throws UserNotFoundException {
        try {
            return userRepository.addBlogsToUser(userId, blog);
        } catch (NodeNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
    }

    public Blog updateBlogOfUser(Long userId, Long blogId, Blog updatedBlog) throws ResourceException {
        Blog blog = getBlogOfUser(userId, blogId);
        blog.update(updatedBlog);
        userRepository.updateBlog(blog);
        return blog;
    }

    public void deleteBlogOfUser(Long userId, Long blogId) throws ResourceException {
        Blog blog = getBlogOfUser(userId, blogId);
        userRepository.deleteBlog(blog);
    }
}
