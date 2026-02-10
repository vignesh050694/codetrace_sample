package com.demo.semesterservice.service;

import com.demo.semesterservice.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    @Autowired
    public UserServiceClient(RestTemplate restTemplate, @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public UserDTO fetchStudentByRollNumber(String rollNumber) {
        String url = userServiceUrl + "/api/users/roll/" + rollNumber;
        try {
            return restTemplate.getForObject(url, UserDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Student not found with roll number: " + rollNumber);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch student from user-service: " + ex.getMessage(), ex);
        }
    }
}

