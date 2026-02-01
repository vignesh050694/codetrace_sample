package com.demo.semesterservice.repository;

import com.demo.semesterservice.model.ArrearResultHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArrearResultHistoryRepository extends JpaRepository<ArrearResultHistory, Long> {

    List<ArrearResultHistory> findByStudentRollNumberAndSemesterAndSubjectOrderByAttemptNumberAsc(
            String studentRollNumber, int semester, String subject);

    List<ArrearResultHistory> findByStudentRollNumberOrderByUpdatedAtDesc(String studentRollNumber);
}
