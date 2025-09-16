package com.studentmgmt.controller;

import com.studentmgmt.dto.GradeDTO;
import com.studentmgmt.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@CrossOrigin(origins = "*")
public class GradeController {
    
    @Autowired
    private GradeService gradeService;
    
    @GetMapping
    public ResponseEntity<List<GradeDTO>> getGrades(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String courseId) {
        
        List<GradeDTO> grades;
        if (studentId != null) {
            grades = gradeService.getGradesByStudentId(studentId);
        } else if (courseId != null) {
            grades = gradeService.getGradesByCourseId(courseId);
        } else {
            grades = List.of();
        }
        
        return ResponseEntity.ok(grades);
    }
    
    @PostMapping
    public ResponseEntity<?> createGrade(@Valid @RequestBody GradeDTO gradeDTO) {
        return gradeService.createGrade(gradeDTO)
                .map(grade -> ResponseEntity.status(HttpStatus.CREATED).body(grade))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable String id, @Valid @RequestBody GradeDTO gradeDTO) {
        return gradeService.updateGrade(id, gradeDTO)
                .map(grade -> ResponseEntity.ok(grade))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable String id) {
        if (gradeService.deleteGrade(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}