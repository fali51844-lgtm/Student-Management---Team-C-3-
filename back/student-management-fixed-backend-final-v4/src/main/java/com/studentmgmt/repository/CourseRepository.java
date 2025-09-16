package com.studentmgmt.repository;

import com.studentmgmt.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByCourseId(String courseId);
    List<Course> findByIsActive(Boolean isActive);
    long countByIsActive(Boolean isActive);
}