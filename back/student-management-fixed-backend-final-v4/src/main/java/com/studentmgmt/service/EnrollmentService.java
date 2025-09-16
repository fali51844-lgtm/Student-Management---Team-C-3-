package com.studentmgmt.service;

import com.studentmgmt.dto.EnrollmentDTO;
import com.studentmgmt.entity.Course;
import com.studentmgmt.entity.Enrollment;
import com.studentmgmt.entity.Student;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.EnrollmentRepository;
import com.studentmgmt.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Return all enrollments as DTOs.
     */
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::convertToDTOWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Create an enrollment for given studentId and courseId.
     * Returns EnrollmentDTO on success, or null on failure (duplicate, missing entities, capacity full).
     */
    @Transactional
    public EnrollmentDTO createEnrollment(String studentId, String courseId) {
        // prevent duplicate
        if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            return null;
        }

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if (studentOpt.isEmpty() || courseOpt.isEmpty()) {
            return null;
        }

        Course course = courseOpt.get();
        long currentEnrollments = enrollmentRepository.countByCourseId(courseId);

        if (course.getCapacity() != null && currentEnrollments >= course.getCapacity()) {
            return null;
        }

        Student student = studentOpt.get();
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);
        return convertToDTOWithDetails(saved);
    }

    /**
     * Delete enrollment by studentId and courseId.
     */
    @Transactional
    public boolean deleteEnrollment(String studentId, String courseId) {
        Optional<Enrollment> enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollment.isPresent()) {
            enrollmentRepository.delete(enrollment.get());
            return true;
        }
        return false;
    }

    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent() != null ? enrollment.getStudent().getId() : null);
        dto.setCourseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null);
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        return dto;
    }

    /**
     * Populate DTO with nested student and course details and friendly names.
     */
    private EnrollmentDTO convertToDTOWithDetails(Enrollment enrollment) {
        EnrollmentDTO dto = convertToDTO(enrollment);

        Student s = enrollment.getStudent();
        if (s != null) {
            com.studentmgmt.dto.StudentDTO sd = new com.studentmgmt.dto.StudentDTO();
            sd.setId(s.getId());
            sd.setStudentId(s.getStudentId());
            sd.setFirstName(s.getFirstName());
            sd.setLastName(s.getLastName());
            sd.setEmail(s.getEmail());
            dto.setStudent(sd);
            String name = ((s.getFirstName() == null ? "" : s.getFirstName()) +
                    (s.getLastName() == null ? "" : " " + s.getLastName())).trim();
            dto.setStudentName(name.isEmpty() ? s.getStudentId() : name);
        }

        Course c = enrollment.getCourse();
        if (c != null) {
            com.studentmgmt.dto.CourseDTO cd = new com.studentmgmt.dto.CourseDTO();
            cd.setId(c.getId());
            cd.setCourseId(c.getCourseId());
            cd.setName(c.getName());
            cd.setDescription(c.getDescription());
            cd.setCredits(c.getCredits());
            cd.setInstructor(c.getInstructor());
            dto.setCourse(cd);
            dto.setCourseName(c.getName());
        }

        return dto;
    }
}
