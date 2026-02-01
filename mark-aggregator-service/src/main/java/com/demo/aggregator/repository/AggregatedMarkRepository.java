package com.demo.aggregator.repository;

import com.demo.aggregator.model.AggregatedMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AggregatedMarkRepository extends JpaRepository<AggregatedMark, Long> {

    List<AggregatedMark> findByStudentRollNumber(String studentRollNumber);

    List<AggregatedMark> findByStudentRollNumberAndSemester(String studentRollNumber, int semester);

    Optional<AggregatedMark> findByStudentRollNumberAndSemesterAndSubject(
            String studentRollNumber, int semester, String subject);

    List<AggregatedMark> findByDepartment(String department);
}
