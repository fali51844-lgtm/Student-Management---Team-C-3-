package com.studentmgmt.service;

import com.studentmgmt.dto.CourseDTO;
import com.studentmgmt.dto.GradeDTO;
import com.studentmgmt.dto.StudentDTO;
import com.studentmgmt.entity.Course;
import com.studentmgmt.entity.Grade;
import com.studentmgmt.entity.Student;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.GradeRepository;
import com.studentmgmt.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Return all grades (with details).
     */
    public List<GradeDTO> getAllGrades() {
        return gradeRepository.findAll()
                .stream()
                .map(this::convertToDTOWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Return grades for a particular student id.
     * In-memory filtering used so repository doesn't need custom methods.
     */
    public List<GradeDTO> getGradesByStudentId(String studentId) {
        if (studentId == null) return List.of();
        return gradeRepository.findAll()
                .stream()
                .filter(g -> studentId.equals(g.getStudentId()))
                .map(this::convertToDTOWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Return grades for a particular course id.
     */
    public List<GradeDTO> getGradesByCourseId(String courseId) {
        if (courseId == null) return List.of();
        return gradeRepository.findAll()
                .stream()
                .filter(g -> courseId.equals(g.getCourseId()))
                .map(this::convertToDTOWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Create a grade entry. Return Optional.of(dto) on success, Optional.empty() on failure.
     */
    @Transactional
    public Optional<GradeDTO> createGrade(GradeDTO dto) {
        try {
            Grade grade = new Grade();
            // copy allowed fields
            grade.setStudentId(dto.getStudentId());
            grade.setCourseId(dto.getCourseId());
            grade.setGrade(dto.getGrade());
            grade.setLetterGrade(dto.getLetterGrade());
            grade.setSemester(dto.getSemester());
            grade.setYear(dto.getYear());
            grade.setCreatedAt(LocalDateTime.now());

            Grade saved = gradeRepository.save(grade);
            return Optional.of(convertToDTOWithDetails(saved));
        } catch (Exception ex) {
            // optionally log the exception
            return Optional.empty();
        }
    }

    /**
     * Update existing grade by id. Returns Optional of updated DTO or empty if grade not found.
     */
    @Transactional
    public Optional<GradeDTO> updateGrade(String id, GradeDTO dto) {
        Optional<Grade> opt = gradeRepository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Grade grade = opt.get();
        // update allowed fields
        if (dto.getStudentId() != null) grade.setStudentId(dto.getStudentId());
        if (dto.getCourseId() != null) grade.setCourseId(dto.getCourseId());
        if (dto.getGrade() != null) grade.setGrade(dto.getGrade());
        if (dto.getLetterGrade() != null) grade.setLetterGrade(dto.getLetterGrade());
        if (dto.getSemester() != null) grade.setSemester(dto.getSemester());
        if (dto.getYear() != null) grade.setYear(dto.getYear());

        Grade saved = gradeRepository.save(grade);
        return Optional.of(convertToDTOWithDetails(saved));
    }

    /**
     * Delete grade by id.
     */
    @Transactional
    public boolean deleteGrade(String id) {
        Optional<Grade> g = gradeRepository.findById(id);
        if (g.isPresent()) {
            gradeRepository.delete(g.get());
            return true;
        }
        return false;
    }

    /* ---------------------- DTO conversion helpers ---------------------- */

    private GradeDTO convertToDTO(Grade grade) {
        GradeDTO dto = new GradeDTO();
        dto.setId(grade.getId());
        dto.setStudentId(grade.getStudentId());
        dto.setCourseId(grade.getCourseId());
        dto.setGrade(grade.getGrade());
        dto.setLetterGrade(grade.getLetterGrade());
        dto.setSemester(grade.getSemester());
        dto.setYear(grade.getYear());
        dto.setCreatedAt(grade.getCreatedAt());
        return dto;
    }

    private GradeDTO convertToDTOWithDetails(Grade grade) {
        GradeDTO dto = convertToDTO(grade);

        // populate student details if available
        Optional<Student> studentOpt = Optional.empty();
        try {
            studentOpt = studentRepository.findById(grade.getStudentId());
        } catch (Exception ignore) {}

        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            StudentDTO sd = new StudentDTO();
            sd.setId(s.getId());
            // copy studentId if entity has it (safe if field absent will be ignored)
            try { sd.setStudentId(s.getStudentId()); } catch (Throwable ignore) {}
            sd.setFirstName(s.getFirstName());
            sd.setLastName(s.getLastName());
            sd.setEmail(s.getEmail());
            dto.setStudent(sd);
            String full = ((s.getFirstName() == null ? "" : s.getFirstName()) + " " + (s.getLastName() == null ? "" : s.getLastName())).trim();
            dto.setStudentName(full.isEmpty() ? null : full);
        }

        // populate course details if available
        Optional<Course> courseOpt = Optional.empty();
        try {
            courseOpt = courseRepository.findById(grade.getCourseId());
        } catch (Exception ignore) {}

        if (courseOpt.isPresent()) {
            Course c = courseOpt.get();
            CourseDTO cd = new CourseDTO();
            cd.setId(c.getId());
            try { cd.setCourseId(c.getCourseId()); } catch (Throwable ignore) {}
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

