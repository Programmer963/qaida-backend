package com.qaida.backend.service;

import com.qaida.backend.dto.LoginRequest;
import com.qaida.backend.dto.RegisterRequest;
import com.qaida.backend.entity.User;
import com.qaida.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public boolean login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) return false;
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }
}
