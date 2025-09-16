package com.studentmgmt.service;

import com.studentmgmt.dto.StudentDTO;
import com.studentmgmt.entity.Grade;
import com.studentmgmt.entity.Student;
import com.studentmgmt.repository.GradeRepository;
import com.studentmgmt.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Added: use GradeRepository to compute averages & any grade-related info
    @Autowired
    private GradeRepository gradeRepository;

    public List<StudentDTO> getAllStudentsWithDetails() {
        return studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<StudentDTO> getStudentById(String id) {
        return studentRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<StudentDTO> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId).map(this::convertToDTO);
    }

    public Optional<StudentDTO> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email).map(this::convertToDTO);
    }

    public List<StudentDTO> searchStudents(String query, String status) {
        return studentRepository.searchStudents(query, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = convertToEntity(studentDTO);
        if (student.getStudentId() == null || student.getStudentId().isEmpty()) {
            student.setStudentId(generateStudentId());
        }
        if (student.getStatus() == null) {
            student.setStatus("active");
        }
        Student savedStudent = studentRepository.save(student);
        return convertToDTO(savedStudent);
    }

    public Optional<StudentDTO> updateStudent(String id, StudentDTO studentDTO) {
        return studentRepository.findById(id).map(student -> {
            updateStudentFields(student, studentDTO);
            Student savedStudent = studentRepository.save(student);
            return convertToDTO(savedStudent);
        });
    }

    public boolean deleteStudent(String id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private String generateStudentId() {
        long count = studentRepository.count() + 1;
        return String.format("STU%03d", count);
    }

    /**
     * Convert Student entity to DTO and compute average grade by querying GradeRepository.
     * This avoids relying on a missing student.getGrades() relationship.
     */
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setStudentId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setAddress(student.getAddress());
        dto.setStatus(student.getStatus());
        dto.setCreatedAt(student.getCreatedAt());

        try {
            // Find grades that reference this student's entity id (grade.getStudentId() stores student.id)
            List<Grade> grades = gradeRepository.findAll()
                    .stream()
                    .filter(g -> g.getStudentId() != null && g.getStudentId().equals(student.getId()))
                    .collect(Collectors.toList());

            if (!grades.isEmpty()) {
                BigDecimal sum = grades.stream()
                        .filter(g -> g.getGrade() != null)
                        .map(Grade::getGrade)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                long countWithNumeric = grades.stream().filter(g -> g.getGrade() != null).count();
                if (countWithNumeric > 0 && sum.compareTo(BigDecimal.ZERO) >= 0) {
                    BigDecimal average = sum.divide(BigDecimal.valueOf(countWithNumeric), 2, RoundingMode.HALF_UP);
                    dto.setAverageGrade(average);
                }
            }
        } catch (Exception ignored) {
            // If GradeRepository or grade fields are not present, we simply skip average computation.
        }

        return dto;
    }

    private Student convertToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setId(dto.getId());
        student.setStudentId(dto.getStudentId());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setAddress(dto.getAddress());
        student.setStatus(dto.getStatus());
        return student;
    }

    private void updateStudentFields(Student student, StudentDTO dto) {
        if (dto.getFirstName() != null) student.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) student.setLastName(dto.getLastName());
        if (dto.getEmail() != null) student.setEmail(dto.getEmail());
        if (dto.getPhone() != null) student.setPhone(dto.getPhone());
        if (dto.getDateOfBirth() != null) student.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getAddress() != null) student.setAddress(dto.getAddress());
        if (dto.getStatus() != null) student.setStatus(dto.getStatus());
    }
}
