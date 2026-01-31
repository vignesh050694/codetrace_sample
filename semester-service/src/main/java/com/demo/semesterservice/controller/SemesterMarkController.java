package com.demo.semesterservice.controller;

import com.demo.semesterservice.model.SemesterMark;
import com.demo.semesterservice.service.SemesterMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
public class SemesterMarkController {

    @Autowired
    private SemesterMarkService semesterMarkService;

    @PostMapping
    public ResponseEntity<SemesterMark> addMarks(@RequestBody SemesterMark mark) {
        return ResponseEntity.ok(semesterMarkService.addMarks(mark));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SemesterMark> updateMarks(@PathVariable Long id, @RequestBody SemesterMark mark) {
        return ResponseEntity.ok(semesterMarkService.updateMarks(id, mark));
    }

    @GetMapping
    public ResponseEntity<List<SemesterMark>> getAllMarks() {
        return ResponseEntity.ok(semesterMarkService.getAllMarks());
    }

    @GetMapping("/student/{rollNumber}")
    public ResponseEntity<List<SemesterMark>> getMarksByRollNumber(@PathVariable String rollNumber) {
        return ResponseEntity.ok(semesterMarkService.getMarksByRollNumber(rollNumber));
    }

    @GetMapping("/student/{rollNumber}/semester/{semester}")
    public ResponseEntity<List<SemesterMark>> getMarksByRollNumberAndSemester(
            @PathVariable String rollNumber, @PathVariable int semester) {
        return ResponseEntity.ok(semesterMarkService.getMarksByRollNumberAndSemester(rollNumber, semester));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarks(@PathVariable Long id) {
        semesterMarkService.deleteMarks(id);
        return ResponseEntity.noContent().build();
    }
}
