package com.studentmgmt.service;

import com.studentmgmt.dto.DashboardStatsDTO;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.GradeRepository;
import com.studentmgmt.repository.StudentRepository;
import com.studentmgmt.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DashboardService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public DashboardStatsDTO getDashboardStats() {
        long totalStudents = studentRepository.count();
        long activeCourses = courseRepository.countByIsActive(true);

        BigDecimal averageGrade = gradeRepository.findAverageGrade()
                .orElse(BigDecimal.ZERO);

        long graduatedStudents = studentRepository.countByStatus("graduated");
        BigDecimal graduationRate = totalStudents > 0 
                ? BigDecimal.valueOf(graduatedStudents)
                    .divide(BigDecimal.valueOf(totalStudents), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        long totalEnrollments = enrollmentRepository.count();

        return new DashboardStatsDTO(totalStudents, activeCourses, averageGrade, graduationRate, totalEnrollments);
    }
}
