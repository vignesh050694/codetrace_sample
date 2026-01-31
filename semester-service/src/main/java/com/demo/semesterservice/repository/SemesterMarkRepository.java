package com.demo.semesterservice.repository;

import com.demo.semesterservice.model.SemesterMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterMarkRepository extends JpaRepository<SemesterMark, Long> {

    List<SemesterMark> findByStudentRollNumber(String studentRollNumber);

    List<SemesterMark> findByStudentRollNumberAndSemester(String studentRollNumber, int semester);
}
