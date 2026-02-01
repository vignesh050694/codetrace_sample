package com.demo.semesterservice.repository;

import com.demo.semesterservice.model.ArrearResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArrearResultRepository extends JpaRepository<ArrearResult, Long> {

    Optional<ArrearResult> findByStudentRollNumberAndSemesterAndSubject(
            String studentRollNumber, int semester, String subject);

    List<ArrearResult> findByStudentRollNumber(String studentRollNumber);

    List<ArrearResult> findByStudentRollNumberAndSemester(String studentRollNumber, int semester);
}
