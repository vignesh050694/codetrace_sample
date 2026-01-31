package com.demo.userservice.service;

import com.demo.userservice.model.User;
import com.demo.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerStudent(User user) {
        user.setRole("STUDENT");
        return userRepository.save(user);
    }

    public User registerProfessor(User user) {
        user.setRole("PROFESSOR");
        user.setRollNumber(null);
        return userRepository.save(user);
    }

    public User login(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByRollNumber(String rollNumber) {
        return userRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("User not found with roll number: " + rollNumber));
    }

    public boolean studentExists(String rollNumber) {
        return userRepository.existsByRollNumber(rollNumber);
    }

    public User updateUser(Long id, User updated) {
        User existing = getUserById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        if (updated.getRollNumber() != null) {
            existing.setRollNumber(updated.getRollNumber());
        }
        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
