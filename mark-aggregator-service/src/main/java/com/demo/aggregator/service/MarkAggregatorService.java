package com.demo.aggregator.service;

import com.demo.aggregator.dto.*;
import com.demo.aggregator.model.AggregatedMark;
import com.demo.aggregator.repository.AggregatedMarkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarkAggregatorService {

    private static final Logger logger = LoggerFactory.getLogger(MarkAggregatorService.class);
    private static final double MAX_MARKS = 100.0;

    @Autowired
    private AggregatedMarkRepository aggregatedMarkRepository;

    @Autowired
    private UserEnrichmentService userEnrichmentService;

    @Autowired
    private NotificationPublisher notificationPublisher;

    public void processMarkEvent(String studentRollNumber, int semester, String subject,
                                  double marks, String source) {
        UserDTO user = userEnrichmentService.getUserByRollNumber(studentRollNumber);

        double percentage = (marks / MAX_MARKS) * 100;
        String status = marks >= 40 ? "PASS" : "FAIL";

        AggregatedMark aggregated = aggregatedMarkRepository
                .findByStudentRollNumberAndSemesterAndSubject(studentRollNumber, semester, subject)
                .orElse(new AggregatedMark());

        aggregated.setStudentRollNumber(studentRollNumber);
        aggregated.setStudentName(user.getName());
        aggregated.setEmail(user.getEmail());
        aggregated.setDepartment(user.getDepartment());
        aggregated.setSemester(semester);
        aggregated.setSubject(subject);
        aggregated.setMarks(marks);
        aggregated.setPercentage(percentage);
        aggregated.setStatus(status);
        aggregated.setSource(source);

        aggregatedMarkRepository.save(aggregated);
        logger.info("Aggregated mark saved for student: {} subject: {} percentage: {}%",
                studentRollNumber, subject, percentage);

        StudentOverallPercentageDTO overall = calculateOverallPercentage(studentRollNumber);

        MarkNotificationDTO notification = new MarkNotificationDTO();
        notification.setEventType(source);
        notification.setStudentRollNumber(studentRollNumber);
        notification.setStudentName(user.getName());
        notification.setEmail(user.getEmail());
        notification.setDepartment(user.getDepartment());
        notification.setSemester(semester);
        notification.setSubject(subject);
        notification.setMarks(marks);
        notification.setPercentage(percentage);
        notification.setStatus(status);
        notification.setOverallPercentage(overall.getOverallPercentage());
        notification.setOverallStatus(overall.getOverallStatus());

        notificationPublisher.publishMarkNotification(notification);
    }

    // 1. Individual student percentage per subject
    public List<StudentSubjectPercentageDTO> getStudentSubjectPercentages(String rollNumber) {
        List<AggregatedMark> marks = aggregatedMarkRepository.findByStudentRollNumber(rollNumber);
        UserDTO user = userEnrichmentService.getUserByRollNumber(rollNumber);

        return marks.stream().map(m -> {
            StudentSubjectPercentageDTO dto = new StudentSubjectPercentageDTO();
            dto.setStudentRollNumber(m.getStudentRollNumber());
            dto.setStudentName(user.getName());
            dto.setDepartment(user.getDepartment());
            dto.setSemester(m.getSemester());
            dto.setSubject(m.getSubject());
            dto.setMarks(m.getMarks());
            dto.setPercentage(m.getPercentage());
            dto.setStatus(m.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    // 2. Overall percentage for each student
    public StudentOverallPercentageDTO calculateOverallPercentage(String rollNumber) {
        List<AggregatedMark> marks = aggregatedMarkRepository.findByStudentRollNumber(rollNumber);
        UserDTO user = userEnrichmentService.getUserByRollNumber(rollNumber);

        StudentOverallPercentageDTO dto = new StudentOverallPercentageDTO();
        dto.setStudentRollNumber(rollNumber);
        dto.setStudentName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDepartment(user.getDepartment());

        if (marks.isEmpty()) {
            dto.setTotalSubjects(0);
            dto.setTotalMarks(0);
            dto.setOverallPercentage(0);
            dto.setOverallStatus("NO_DATA");
            return dto;
        }

        double totalMarks = marks.stream().mapToDouble(AggregatedMark::getMarks).sum();
        double maxPossible = marks.size() * MAX_MARKS;
        double overallPercentage = (totalMarks / maxPossible) * 100;
        boolean allPassed = marks.stream().allMatch(m -> m.getMarks() >= 40);

        dto.setTotalSubjects(marks.size());
        dto.setTotalMarks(totalMarks);
        dto.setOverallPercentage(Math.round(overallPercentage * 100.0) / 100.0);
        dto.setOverallStatus(allPassed ? "PASS" : "FAIL");

        return dto;
    }

    // Get overall percentages for all students
    public List<StudentOverallPercentageDTO> getAllStudentsOverallPercentages() {
        List<AggregatedMark> allMarks = aggregatedMarkRepository.findAll();

        Map<String, List<AggregatedMark>> byStudent = allMarks.stream()
                .collect(Collectors.groupingBy(AggregatedMark::getStudentRollNumber));

        return byStudent.keySet().stream()
                .map(this::calculateOverallPercentage)
                .collect(Collectors.toList());
    }

    // 3. Department-wise performance dashboard
    public List<DepartmentPerformanceDTO> getDepartmentPerformance() {
        List<AggregatedMark> allMarks = aggregatedMarkRepository.findAll();

        Map<String, List<AggregatedMark>> byDepartment = allMarks.stream()
                .filter(m -> m.getDepartment() != null && !m.getDepartment().equals("Unknown"))
                .collect(Collectors.groupingBy(AggregatedMark::getDepartment));

        List<DepartmentPerformanceDTO> result = new ArrayList<>();

        for (Map.Entry<String, List<AggregatedMark>> entry : byDepartment.entrySet()) {
            String department = entry.getKey();
            List<AggregatedMark> deptMarks = entry.getValue();

            DepartmentPerformanceDTO dto = new DepartmentPerformanceDTO();
            dto.setDepartment(department);

            Set<String> uniqueStudents = deptMarks.stream()
                    .map(AggregatedMark::getStudentRollNumber)
                    .collect(Collectors.toSet());
            dto.setTotalStudents(uniqueStudents.size());
            dto.setTotalSubjectEntries(deptMarks.size());

            double avgPercentage = deptMarks.stream()
                    .mapToDouble(AggregatedMark::getPercentage)
                    .average()
                    .orElse(0);
            dto.setAveragePercentage(Math.round(avgPercentage * 100.0) / 100.0);

            long passCount = deptMarks.stream().filter(m -> "PASS".equals(m.getStatus())).count();
            long failCount = deptMarks.stream().filter(m -> "FAIL".equals(m.getStatus())).count();
            dto.setPassCount((int) passCount);
            dto.setFailCount((int) failCount);
            dto.setPassPercentage(
                    Math.round(((double) passCount / deptMarks.size()) * 100 * 100.0) / 100.0
            );

            double highest = deptMarks.stream()
                    .mapToDouble(AggregatedMark::getPercentage)
                    .max()
                    .orElse(0);
            double lowest = deptMarks.stream()
                    .mapToDouble(AggregatedMark::getPercentage)
                    .min()
                    .orElse(0);
            dto.setHighestPercentage(highest);
            dto.setLowestPercentage(lowest);

            result.add(dto);
        }

        return result;
    }

    // Department performance for a specific department
    public DepartmentPerformanceDTO getDepartmentPerformanceByName(String department) {
        return getDepartmentPerformance().stream()
                .filter(d -> d.getDepartment().equalsIgnoreCase(department))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No data found for department: " + department));
    }
}
