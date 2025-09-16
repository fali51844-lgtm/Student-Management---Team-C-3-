package com.studentmgmt.service;

import com.studentmgmt.dto.CourseDTO;
import com.studentmgmt.entity.Course;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    public List<CourseDTO> getAllCoursesWithDetails() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<CourseDTO> getCourseById(String id) {
        return courseRepository.findById(id).map(this::convertToDTO);
    }
    
    public Optional<CourseDTO> getCourseByCourseId(String courseId) {
        return courseRepository.findByCourseId(courseId).map(this::convertToDTO);
    }
    
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = convertToEntity(courseDTO);
        if (course.getIsActive() == null) {
            course.setIsActive(true);
        }
        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }
    
    public Optional<CourseDTO> updateCourse(String id, CourseDTO courseDTO) {
        return courseRepository.findById(id).map(course -> {
            updateCourseFields(course, courseDTO);
            Course savedCourse = courseRepository.save(course);
            return convertToDTO(savedCourse);
        });
    }
    
    public boolean deleteCourse(String id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseId(course.getCourseId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setInstructor(course.getInstructor());
        dto.setCapacity(course.getCapacity());
        dto.setCredits(course.getCredits());
        dto.setIsActive(course.getIsActive());
        dto.setCreatedAt(course.getCreatedAt());
        
        long enrolledCount = enrollmentRepository.countByCourseId(course.getId());
        dto.setEnrolledCount((int) enrolledCount);
        
        return dto;
    }
    
    private Course convertToEntity(CourseDTO dto) {
        Course course = new Course();
        course.setId(dto.getId());
        course.setCourseId(dto.getCourseId());
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setInstructor(dto.getInstructor());
        course.setCapacity(dto.getCapacity());
        course.setCredits(dto.getCredits());
        course.setIsActive(dto.getIsActive());
        return course;
    }
    
    private void updateCourseFields(Course course, CourseDTO dto) {
        if (dto.getName() != null) course.setName(dto.getName());
        if (dto.getDescription() != null) course.setDescription(dto.getDescription());
        if (dto.getInstructor() != null) course.setInstructor(dto.getInstructor());
        if (dto.getCapacity() != null) course.setCapacity(dto.getCapacity());
        if (dto.getCredits() != null) course.setCredits(dto.getCredits());
        if (dto.getIsActive() != null) course.setIsActive(dto.getIsActive());
    }
}