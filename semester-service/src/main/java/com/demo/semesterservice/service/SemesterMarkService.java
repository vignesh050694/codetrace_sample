package com.demo.semesterservice.service;

import com.demo.semesterservice.model.SemesterMark;
import com.demo.semesterservice.repository.SemesterMarkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SemesterMarkService {

    @Autowired
    private SemesterMarkRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${kafka.topic.mark-created}")
    private String markCreatedTopic;

    @Value("${kafka.topic.mark-updated}")
    private String markUpdatedTopic;

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

    public SemesterMark addMarks(SemesterMark mark) {
        if (!studentExists(mark.getStudentRollNumber())) {
            throw new RuntimeException("Student with roll number " + mark.getStudentRollNumber() + " not found");
        }
        SemesterMark saved = repository.save(mark);
        publishMarkEvent("CREATED", saved);
        return saved;
    }

    public SemesterMark updateMarks(Long id, SemesterMark updated) {
        SemesterMark existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark record not found with id: " + id));
        existing.setSemester(updated.getSemester());
        existing.setSubject(updated.getSubject());
        existing.setMarks(updated.getMarks());
        SemesterMark saved = repository.save(existing);
        publishMarkEvent("UPDATED", saved);
        return saved;
    }

    public List<SemesterMark> getAllMarks() {
        return repository.findAll();
    }

    public List<SemesterMark> getMarksByRollNumber(String rollNumber) {
        return repository.findByStudentRollNumber(rollNumber);
    }

    public List<SemesterMark> getMarksByRollNumberAndSemester(String rollNumber, int semester) {
        return repository.findByStudentRollNumberAndSemester(rollNumber, semester);
    }

    public void deleteMarks(Long id) {
        repository.deleteById(id);
    }

    private void publishMarkEvent(String eventType, SemesterMark mark) {
        SemesterMarkEvent event = new SemesterMarkEvent();
        event.setEventType(eventType);
        event.setStudentRollNumber(mark.getStudentRollNumber());
        event.setSemester(mark.getSemester());
        event.setSubject(mark.getSubject());
        event.setMarks(mark.getMarks());

        try {
            String message = objectMapper.writeValueAsString(event);
            String topic = "CREATED".equals(eventType) ? markCreatedTopic : markUpdatedTopic;
            kafkaTemplate.send(topic, mark.getStudentRollNumber(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize mark event", e);
        }
    }
}
