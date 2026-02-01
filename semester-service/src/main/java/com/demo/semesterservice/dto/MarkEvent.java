package com.demo.semesterservice.dto;

import java.time.LocalDateTime;

public class MarkEvent {

    private String studentRollNumber;
    private int semester;
    private String subject;
    private double marks;
    private LocalDateTime timestamp;

    public MarkEvent() {
    }

    public MarkEvent(String studentRollNumber, int semester, String subject, double marks) {
        this.studentRollNumber = studentRollNumber;
        this.semester = semester;
        this.subject = subject;
        this.marks = marks;
        this.timestamp = LocalDateTime.now();
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
