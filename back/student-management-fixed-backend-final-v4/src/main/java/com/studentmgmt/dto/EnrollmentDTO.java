package com.studentmgmt.dto;

import java.time.LocalDateTime;

public class EnrollmentDTO {
    private String id;
    private String studentId;
    private String courseId;
    private LocalDateTime enrolledAt;

    // friendly fields
    private String studentName;
    private String courseName;

    // nested details
    private StudentDTO student;
    private CourseDTO course;

    public EnrollmentDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public StudentDTO getStudent() { return student; }
    public void setStudent(StudentDTO student) { this.student = student; }

    public CourseDTO getCourse() { return course; }
    public void setCourse(CourseDTO course) { this.course = course; }
}
