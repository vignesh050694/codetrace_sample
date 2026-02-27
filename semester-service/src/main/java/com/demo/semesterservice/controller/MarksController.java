package com.demo.semesterservice.controller;

import com.demo.semesterservice.dto.BatchMarkRequestDTO;
import com.demo.semesterservice.dto.MarkRequestDTO;
import com.demo.semesterservice.service.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/marks")
public class MarksController {

    private final MarkService markService;

    @Autowired
    public MarksController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<List<String>> createOrUpdateMarks(@RequestBody MarkRequestDTO request) {
        List<String> results = markService.processMarkRequest(request);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<String>> createOrUpdateMarksBatch(@RequestBody BatchMarkRequestDTO batchRequest) {
        List<String> allResults = new ArrayList<>();
        if (batchRequest.getStudents() == null) {
            return ResponseEntity.badRequest().body(List.of("students required"));
        }

        for (BatchMarkRequestDTO.StudentMarks sm : batchRequest.getStudents()) {
            MarkRequestDTO req = new MarkRequestDTO();
            req.setRollNumber(sm.getRollNumber());
            req.setSemester(batchRequest.getSemester());
            req.setSubjects(sm.getSubjects());

            allResults.addAll(markService.processMarkRequest(req));
        }

        return ResponseEntity.ok(allResults);
    }
}
