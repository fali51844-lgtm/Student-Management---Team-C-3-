package com.studentmgmt.repository;

import com.studentmgmt.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, String> {
    List<Grade> findByStudentId(String studentId);
    List<Grade> findByCourseId(String courseId);
    Optional<Grade> findByStudentIdAndCourseIdAndSemesterAndYear(String studentId, String courseId, String semester, Integer year);
    
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.grade IS NOT NULL")
    Optional<BigDecimal> findAverageGrade();
}