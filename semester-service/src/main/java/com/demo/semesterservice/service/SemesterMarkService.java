package com.demo.semesterservice.service;

import com.demo.semesterservice.dto.MarkEvent;
import com.demo.semesterservice.model.SemesterMark;
import com.demo.semesterservice.repository.SemesterMarkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SemesterMarkService {

    private static final Logger log = LoggerFactory.getLogger(SemesterMarkService.class);

    @Autowired
    private SemesterMarkRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, MarkEvent> kafkaTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${kafka.topic.marks}")
    private String marksTopic;

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
        SemesterMark savedMark = repository.save(mark);

        // Publish to Kafka
        MarkEvent event = new MarkEvent(
                savedMark.getStudentRollNumber(),
                savedMark.getSemester(),
                savedMark.getSubject(),
                savedMark.getMarks()
        );
        kafkaTemplate.send(marksTopic, savedMark.getStudentRollNumber(), event);
        log.info("Published mark event to Kafka topic '{}': rollNumber={}, subject={}, marks={}",
                marksTopic, savedMark.getStudentRollNumber(), savedMark.getSubject(), savedMark.getMarks());

        return savedMark;
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
        MarkEvent event = new MarkEvent(
                mark.getStudentRollNumber(),
                mark.getSemester(),
                mark.getSubject(),
                mark.getMarks()
        );
        kafkaTemplate.send(marksTopic, mark.getStudentRollNumber(), event);
        log.info("Published mark {} event to Kafka topic '{}': rollNumber={}, subject={}, marks={}",
                eventType, marksTopic, mark.getStudentRollNumber(), mark.getSubject(), mark.getMarks());
    }
}
