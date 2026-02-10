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

    public User getUserByRollNumber(String rollNumber) {
        return userRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("User not found with roll number: " + rollNumber));
    }

}
