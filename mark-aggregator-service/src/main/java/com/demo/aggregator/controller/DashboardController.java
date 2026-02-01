package com.demo.aggregator.controller;

import com.demo.aggregator.dto.DepartmentPerformanceDTO;
import com.demo.aggregator.dto.StudentOverallPercentageDTO;
import com.demo.aggregator.dto.StudentSubjectPercentageDTO;
import com.demo.aggregator.service.MarkAggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private MarkAggregatorService markAggregatorService;

    // 1. Individual student percentage per subject
    @GetMapping("/student/{rollNumber}/subjects")
    public ResponseEntity<List<StudentSubjectPercentageDTO>> getStudentSubjectPercentages(
            @PathVariable String rollNumber) {
        return ResponseEntity.ok(markAggregatorService.getStudentSubjectPercentages(rollNumber));
    }

    // 2. Overall percentage for a specific student
    @GetMapping("/student/{rollNumber}/overall")
    public ResponseEntity<StudentOverallPercentageDTO> getStudentOverallPercentage(
            @PathVariable String rollNumber) {
        return ResponseEntity.ok(markAggregatorService.calculateOverallPercentage(rollNumber));
    }

    // 2. Overall percentages for all students
    @GetMapping("/students/overall")
    public ResponseEntity<List<StudentOverallPercentageDTO>> getAllStudentsOverallPercentages() {
        return ResponseEntity.ok(markAggregatorService.getAllStudentsOverallPercentages());
    }

    // 3. Department-wise performance - all departments
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentPerformanceDTO>> getDepartmentPerformance() {
        return ResponseEntity.ok(markAggregatorService.getDepartmentPerformance());
    }

    // 3. Department-wise performance - specific department
    @GetMapping("/departments/{department}")
    public ResponseEntity<DepartmentPerformanceDTO> getDepartmentPerformanceByName(
            @PathVariable String department) {
        return ResponseEntity.ok(markAggregatorService.getDepartmentPerformanceByName(department));
    }
}
