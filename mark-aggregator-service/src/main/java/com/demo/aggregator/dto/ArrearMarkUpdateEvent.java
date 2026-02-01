package com.demo.aggregator.dto;

public class ArrearMarkUpdateEvent {

    private String studentRollNumber;
    private int semester;
    private String subject;
    private double previousMarks;
    private double newMarks;
    private int attemptNumber;
    private String status;
    private String updatedAt;

    public ArrearMarkUpdateEvent() {
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

    public double getPreviousMarks() {
        return previousMarks;
    }

    public void setPreviousMarks(double previousMarks) {
        this.previousMarks = previousMarks;
    }

    public double getNewMarks() {
        return newMarks;
    }

    public void setNewMarks(double newMarks) {
        this.newMarks = newMarks;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
