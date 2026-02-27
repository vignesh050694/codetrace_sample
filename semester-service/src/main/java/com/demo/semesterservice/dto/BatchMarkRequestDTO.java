package com.demo.semesterservice.dto;

import java.util.List;

public class BatchMarkRequestDTO {
    private String semester;
    private List<StudentMarks> students;

    public static class StudentMarks {
        private String rollNumber;
        private List<MarkRequestDTO.SubjectMark> subjects;

        public StudentMarks() {}

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public List<MarkRequestDTO.SubjectMark> getSubjects() {
            return subjects;
        }

        public void setSubjects(List<MarkRequestDTO.SubjectMark> subjects) {
            this.subjects = subjects;
        }
    }

    public BatchMarkRequestDTO() {}

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<StudentMarks> getStudents() {
        return students;
    }

    public void setStudents(List<StudentMarks> students) {
        this.students = students;
    }
}

