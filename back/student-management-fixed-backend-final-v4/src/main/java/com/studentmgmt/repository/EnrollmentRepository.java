package com.studentmgmt.repository;

import com.studentmgmt.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    List<Enrollment> findByStudentId(String studentId);
    List<Enrollment> findByCourseId(String courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId);
    void deleteByStudentIdAndCourseId(String studentId, String courseId);
    long countByCourseId(String courseId);
}