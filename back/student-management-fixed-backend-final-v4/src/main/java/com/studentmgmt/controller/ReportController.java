package com.studentmgmt.controller;

import com.studentmgmt.dto.CourseDTO;
import com.studentmgmt.dto.StudentDTO;
import com.studentmgmt.service.CourseService;
import com.studentmgmt.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CourseService courseService;
    
    @GetMapping("/students-csv")
    public ResponseEntity<String> getStudentsCSV() {
        List<StudentDTO> students = studentService.getAllStudentsWithDetails();
        
        StringBuilder csv = new StringBuilder();
        csv.append("Student ID,First Name,Last Name,Email,Phone,Status,Average Grade,Enrolled Courses\n");
        
        for (StudentDTO student : students) {
            String courses = student.getCourses() != null 
                    ? student.getCourses().stream()
                        .map(c -> c.getName())
                        .collect(Collectors.joining("; "))
                    : "";
            
            csv.append(String.format("%s,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getPhone() != null ? student.getPhone() : "",
                    student.getStatus(),
                    student.getAverageGrade() != null ? student.getAverageGrade().toString() : "N/A",
                    courses));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "students.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }
    
    @GetMapping("/courses-csv")
    public ResponseEntity<String> getCoursesCSV() {
        List<CourseDTO> courses = courseService.getAllCoursesWithDetails();
        
        StringBuilder csv = new StringBuilder();
        csv.append("Course ID,Course Name,Instructor,Capacity,Enrolled,Credits,Status\n");
        
        for (CourseDTO course : courses) {
            csv.append(String.format("%s,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    course.getCourseId(),
                    course.getName(),
                    course.getInstructor(),
                    course.getCapacity(),
                    course.getEnrolledCount() != null ? course.getEnrolledCount() : 0,
                    course.getCredits(),
                    course.getIsActive() ? "Active" : "Inactive"));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "courses.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }
}