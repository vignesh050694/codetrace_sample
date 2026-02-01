package com.demo.semesterservice.controller;

import com.demo.semesterservice.model.ArrearResult;
import com.demo.semesterservice.model.ArrearResultHistory;
import com.demo.semesterservice.service.ArrearResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arrears")
public class ArrearResultController {

    @Autowired
    private ArrearResultService arrearResultService;

    @PutMapping("/student/{rollNumber}/semester/{semester}/subject/{subject}")
    public ResponseEntity<ArrearResult> updateArrearResult(
            @PathVariable String rollNumber,
            @PathVariable int semester,
            @PathVariable String subject,
            @RequestParam double marks) {
        return ResponseEntity.ok(arrearResultService.updateArrearResult(rollNumber, semester, subject, marks));
    }

    @GetMapping("/student/{rollNumber}")
    public ResponseEntity<List<ArrearResult>> getArrearsByRollNumber(@PathVariable String rollNumber) {
        return ResponseEntity.ok(arrearResultService.getArrearsByRollNumber(rollNumber));
    }

    @GetMapping("/student/{rollNumber}/semester/{semester}")
    public ResponseEntity<List<ArrearResult>> getArrearsByRollNumberAndSemester(
            @PathVariable String rollNumber, @PathVariable int semester) {
        return ResponseEntity.ok(arrearResultService.getArrearsByRollNumberAndSemester(rollNumber, semester));
    }

    @GetMapping("/history/student/{rollNumber}/semester/{semester}/subject/{subject}")
    public ResponseEntity<List<ArrearResultHistory>> getArrearHistory(
            @PathVariable String rollNumber,
            @PathVariable int semester,
            @PathVariable String subject) {
        return ResponseEntity.ok(arrearResultService.getArrearHistory(rollNumber, semester, subject));
    }

    @GetMapping("/history/student/{rollNumber}")
    public ResponseEntity<List<ArrearResultHistory>> getAllArrearHistory(@PathVariable String rollNumber) {
        return ResponseEntity.ok(arrearResultService.getAllArrearHistoryByRollNumber(rollNumber));
    }
}
