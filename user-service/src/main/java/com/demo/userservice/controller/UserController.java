package com.demo.userservice.controller;

import com.demo.userservice.model.User;
import com.demo.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/roll/{rollNumber}")
    public ResponseEntity<User> getUserByRollNumber(@PathVariable String rollNumber) {
        return ResponseEntity.ok(userService.getUserByRollNumber(rollNumber));
    }

}
