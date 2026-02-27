package com.demo.semesterservice.repository;

import com.demo.semesterservice.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByStudentIdAndSemesterAndSubject(Long studentId, String semester, String subject);
}

