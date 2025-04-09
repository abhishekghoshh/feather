package io.github.abhishekghoshh.crud.service;

import io.github.abhishekghoshh.crud.exception.UserNotFoundException;
import io.github.abhishekghoshh.crud.model.User;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeAlreadyExistingException;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeNotFoundException;
import io.github.abhishekghoshh.crud.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUserById(Long userId) {
        try {
            return userRepository.getUserById(userId);
        } catch (NodeNotFoundException e) {
            throw new UserNotFoundException("User not found with id: " + userId, e);
        }
    }

    public User saveUser(User user) throws NodeAlreadyExistingException {
        return userRepository.saveUser(user);
    }

    public User updateUser(Long userId, User user) throws NodeNotFoundException, NodeAlreadyExistingException {
        User existingUser = userRepository.getUserById(userId);
        if (user.getName() != null) existingUser.setName(user.getName());
        if (user.getAge() != 0) existingUser.setAge(user.getAge());
        if (user.getEmail() != null) {
            if (userRepository.findUserByEmail(user).isPresent() &&
                    !user.getEmail().equals(existingUser.getEmail()))
                throw new NodeAlreadyExistingException("email id already present");
            existingUser.setEmail(user.getEmail());
        }
        return userRepository.updateUser(existingUser);
    }

    public void deleteUser(Long userId) throws NodeNotFoundException {
        userRepository.deleteUser(userId);
    }
}
