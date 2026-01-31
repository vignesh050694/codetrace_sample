package com.demo.userservice.controller;

import com.demo.userservice.model.User;
import com.demo.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register/student")
    public ResponseEntity<User> registerStudent(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerStudent(user));
    }

    @PostMapping("/register/professor")
    public ResponseEntity<User> registerProfessor(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerProfessor(user));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email) {
        return ResponseEntity.ok(userService.login(email));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/roll/{rollNumber}")
    public ResponseEntity<User> getUserByRollNumber(@PathVariable String rollNumber) {
        return ResponseEntity.ok(userService.getUserByRollNumber(rollNumber));
    }

    @GetMapping("/exists/{rollNumber}")
    public ResponseEntity<Boolean> checkStudentExists(@PathVariable String rollNumber) {
        return ResponseEntity.ok(userService.studentExists(rollNumber));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
