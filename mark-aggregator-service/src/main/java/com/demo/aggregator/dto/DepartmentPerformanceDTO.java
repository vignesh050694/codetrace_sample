package com.demo.aggregator.dto;

public class DepartmentPerformanceDTO {

    private String department;
    private int totalStudents;
    private int totalSubjectEntries;
    private double averagePercentage;
    private int passCount;
    private int failCount;
    private double passPercentage;
    private double highestPercentage;
    private double lowestPercentage;

    public DepartmentPerformanceDTO() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalSubjectEntries() {
        return totalSubjectEntries;
    }

    public void setTotalSubjectEntries(int totalSubjectEntries) {
        this.totalSubjectEntries = totalSubjectEntries;
    }

    public double getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(double averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(double passPercentage) {
        this.passPercentage = passPercentage;
    }

    public double getHighestPercentage() {
        return highestPercentage;
    }

    public void setHighestPercentage(double highestPercentage) {
        this.highestPercentage = highestPercentage;
    }

    public double getLowestPercentage() {
        return lowestPercentage;
    }

    public void setLowestPercentage(double lowestPercentage) {
        this.lowestPercentage = lowestPercentage;
    }
}
