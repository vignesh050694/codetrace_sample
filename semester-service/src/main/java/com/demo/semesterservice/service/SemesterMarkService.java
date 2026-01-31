package com.demo.semesterservice.service;

import com.demo.semesterservice.model.SemesterMark;
import com.demo.semesterservice.repository.SemesterMarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SemesterMarkService {

    @Autowired
    private SemesterMarkRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

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
        return repository.save(mark);
    }

    public SemesterMark updateMarks(Long id, SemesterMark updated) {
        SemesterMark existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark record not found with id: " + id));
        existing.setSemester(updated.getSemester());
        existing.setSubject(updated.getSubject());
        existing.setMarks(updated.getMarks());
        return repository.save(existing);
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
}
