package com.demo.semesterservice.dto;

import java.util.List;

public class MarkRequestDTO {
    private String rollNumber; // provided by client
    private String semester;
    private List<SubjectMark> subjects;

    public static class SubjectMark {
        private String subject;
        private Integer mark;

        public SubjectMark() {}

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Integer getMark() {
            return mark;
        }

        public void setMark(Integer mark) {
            this.mark = mark;
        }
    }

    public MarkRequestDTO() {}

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<SubjectMark> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectMark> subjects) {
        this.subjects = subjects;
    }
}

