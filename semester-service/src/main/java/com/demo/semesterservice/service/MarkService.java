package com.demo.semesterservice.service;

import com.demo.semesterservice.dto.MarkEventDTO;
import com.demo.semesterservice.dto.MarkRequestDTO;
import com.demo.semesterservice.dto.UserDTO;
import com.demo.semesterservice.model.Mark;
import com.demo.semesterservice.repository.MarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkService {

    private final MarkRepository markRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String markCreatedTopic;
    private final String markUpdatedTopic;

    @Autowired
    public MarkService(MarkRepository markRepository,
                       UserServiceClient userServiceClient,
                       KafkaTemplate<String, Object> kafkaTemplate,
                       @Value("${kafka.topic.mark-created}") String markCreatedTopic,
                       @Value("${kafka.topic.mark-updated}") String markUpdatedTopic) {
        this.markRepository = markRepository;
        this.userServiceClient = userServiceClient;
        this.kafkaTemplate = kafkaTemplate;
        this.markCreatedTopic = markCreatedTopic;
        this.markUpdatedTopic = markUpdatedTopic;
    }

    @Transactional
    public List<String> processMarkRequest(MarkRequestDTO request) {
        List<String> results = new ArrayList<>();

        // 1. Fetch student
        UserDTO user = userServiceClient.fetchStudentByRollNumber(request.getRollNumber());
        if (user == null || user.getId() == null) {
            String err = "Student not found for roll: " + request.getRollNumber();
            results.add(err);
            publishFailedEvent(request.getRollNumber(), request.getSemester(), null, err);
            return results;
        }

        Long studentId = user.getId();

        for (MarkRequestDTO.SubjectMark sm : request.getSubjects()) {
            try {
                Optional<Mark> existing = markRepository.findByStudentIdAndSemesterAndSubject(studentId, request.getSemester(), sm.getSubject());
                if (existing.isPresent()) {
                    Mark mark = existing.get();
                    mark.setMark(sm.getMark());
                    mark.setRollNumber(request.getRollNumber());
                    Mark saved = markRepository.save(mark);

                    MarkEventDTO event = new MarkEventDTO();
                    event.setStatus("UPDATED");
                    event.setMessage("Mark updated");
                    event.setMarkId(saved.getId());
                    event.setRollNumber(request.getRollNumber());
                    event.setSemester(request.getSemester());
                    event.setSubject(sm.getSubject());
                    kafkaTemplate.send(markUpdatedTopic, event);

                    results.add("Updated " + sm.getSubject());
                } else {
                    Mark mark = new Mark();
                    mark.setStudentId(studentId);
                    mark.setRollNumber(request.getRollNumber());
                    mark.setSemester(request.getSemester());
                    mark.setSubject(sm.getSubject());
                    mark.setMark(sm.getMark());

                    Mark saved = markRepository.save(mark);

                    MarkEventDTO event = new MarkEventDTO();
                    event.setStatus("CREATED");
                    event.setMessage("Mark created");
                    event.setMarkId(saved.getId());
                    event.setRollNumber(request.getRollNumber());
                    event.setSemester(request.getSemester());
                    event.setSubject(sm.getSubject());
                    kafkaTemplate.send(markCreatedTopic, event);

                    results.add("Created " + sm.getSubject());
                }
            } catch (Exception ex) {
                String err = "Failed to process subject " + sm.getSubject() + ": " + ex.getMessage();
                results.add(err);
                publishFailedEvent(request.getRollNumber(), request.getSemester(), sm.getSubject(), err);
            }
        }

        return results;
    }

    private void publishFailedEvent(String roll, String semester, String subject, String message) {
        MarkEventDTO event = new MarkEventDTO();
        event.setStatus("FAILED");
        event.setMessage(message);
        event.setRollNumber(roll);
        event.setSemester(semester);
        event.setSubject(subject);
        kafkaTemplate.send(markCreatedTopic, event); // send to created topic for failures as well
    }
}

