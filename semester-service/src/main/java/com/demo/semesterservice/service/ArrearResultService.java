package com.demo.semesterservice.service;

import com.demo.semesterservice.model.ArrearResult;
import com.demo.semesterservice.model.ArrearResultHistory;
import com.demo.semesterservice.repository.ArrearResultHistoryRepository;
import com.demo.semesterservice.repository.ArrearResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArrearResultService {

    @Autowired
    private ArrearResultRepository arrearResultRepository;

    @Autowired
    private ArrearResultHistoryRepository arrearResultHistoryRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${kafka.topic.arrear-mark-update}")
    private String arrearMarkUpdateTopic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean studentExists(String rollNumber) {
        try {
            Boolean exists = restTemplate.getForObject(
                    userServiceUrl + "/api/users/exists/" + rollNumber,
                    Boolean.class
            );
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            throw new RuntimeException("User Service unavailable: " + e.getMessage());
        }
    }

    @Transactional
    public ArrearResult updateArrearResult(String rollNumber, int semester, String subject, double marks) {
        if (!studentExists(rollNumber)) {
            throw new RuntimeException("Student with roll number " + rollNumber + " not found");
        }

        ArrearResult arrearResult = arrearResultRepository
                .findByStudentRollNumberAndSemesterAndSubject(rollNumber, semester, subject)
                .orElse(null);

        double previousMarks = 0;
        int attemptNumber;

        if (arrearResult == null) {
            arrearResult = new ArrearResult();
            arrearResult.setStudentRollNumber(rollNumber);
            arrearResult.setSemester(semester);
            arrearResult.setSubject(subject);
            attemptNumber = 1;
        } else {
            previousMarks = arrearResult.getMarks();
            attemptNumber = arrearResult.getAttemptNumber() + 1;
        }

        String status = marks >= 40 ? "PASS" : "FAIL";

        arrearResult.setMarks(marks);
        arrearResult.setAttemptNumber(attemptNumber);
        arrearResult.setStatus(status);
        arrearResult.setUpdatedAt(LocalDateTime.now());

        ArrearResult saved = arrearResultRepository.save(arrearResult);

        ArrearResultHistory history = new ArrearResultHistory();
        history.setStudentRollNumber(rollNumber);
        history.setSemester(semester);
        history.setSubject(subject);
        history.setMarks(marks);
        history.setAttemptNumber(attemptNumber);
        history.setStatus(status);
        history.setUpdatedAt(saved.getUpdatedAt());
        arrearResultHistoryRepository.save(history);

        publishArrearMarkUpdateEvent(rollNumber, semester, subject, previousMarks, marks, attemptNumber, status, saved.getUpdatedAt());

        return saved;
    }

    public List<ArrearResult> getArrearsByRollNumber(String rollNumber) {
        return arrearResultRepository.findByStudentRollNumber(rollNumber);
    }

    public List<ArrearResult> getArrearsByRollNumberAndSemester(String rollNumber, int semester) {
        return arrearResultRepository.findByStudentRollNumberAndSemester(rollNumber, semester);
    }

    public List<ArrearResultHistory> getArrearHistory(String rollNumber, int semester, String subject) {
        return arrearResultHistoryRepository
                .findByStudentRollNumberAndSemesterAndSubjectOrderByAttemptNumberAsc(rollNumber, semester, subject);
    }

    public List<ArrearResultHistory> getAllArrearHistoryByRollNumber(String rollNumber) {
        return arrearResultHistoryRepository.findByStudentRollNumberOrderByUpdatedAtDesc(rollNumber);
    }

    private void publishArrearMarkUpdateEvent(String rollNumber, int semester, String subject,
                                               double previousMarks, double newMarks,
                                               int attemptNumber, String status, LocalDateTime updatedAt) {
        ArrearMarkUpdateEvent event = new ArrearMarkUpdateEvent();
        event.setStudentRollNumber(rollNumber);
        event.setSemester(semester);
        event.setSubject(subject);
        event.setPreviousMarks(previousMarks);
        event.setNewMarks(newMarks);
        event.setAttemptNumber(attemptNumber);
        event.setStatus(status);
        event.setUpdatedAt(updatedAt.toString());

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(arrearMarkUpdateTopic, rollNumber, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize arrear mark update event", e);
        }
    }
}
