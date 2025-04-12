package com.qaida.backend.service;

import com.qaida.backend.entity.User;
import com.qaida.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User temp = getUserById(id);
        temp.setUsername(user.getUsername());
        temp.setCity(user.getCity());
        return userRepository.save(temp);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
