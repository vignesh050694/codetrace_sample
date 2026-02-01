package com.demo.aggregator.service;

import com.demo.aggregator.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserEnrichmentService {

    private static final Logger logger = LoggerFactory.getLogger(UserEnrichmentService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserDTO getUserByRollNumber(String rollNumber) {
        try {
            UserDTO user = restTemplate.getForObject(
                    userServiceUrl + "/api/users/roll/" + rollNumber,
                    UserDTO.class
            );
            return user;
        } catch (Exception e) {
            logger.error("Failed to fetch user details for roll number {}: {}", rollNumber, e.getMessage());
            UserDTO fallback = new UserDTO();
            fallback.setRollNumber(rollNumber);
            fallback.setName("Unknown");
            fallback.setDepartment("Unknown");
            fallback.setEmail("Unknown");
            return fallback;
        }
    }
}
