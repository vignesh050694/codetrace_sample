package com.demo.semesterservice.controller;

import com.demo.semesterservice.dto.UserDTO;
import com.demo.semesterservice.service.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentsController {

    private final UserServiceClient userServiceClient;

    @Autowired
    public StudentsController(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @GetMapping("/roll/{rollNumber}")
    public ResponseEntity<UserDTO> getStudentByRollNumber(@PathVariable String rollNumber) {
        UserDTO user = userServiceClient.fetchStudentByRollNumber(rollNumber);
        return ResponseEntity.ok(user);
    }
}
