package com.studentmgmt.controller;

import com.studentmgmt.dto.EnrollmentDTO;
import com.studentmgmt.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        List<EnrollmentDTO> list = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> createEnrollment(@RequestBody Map<String, String> payload) {
        String studentId = payload.get("studentId");
        String courseId = payload.get("courseId");
        EnrollmentDTO created = enrollmentService.createEnrollment(studentId, courseId);
        if (created != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Unable to create enrollment"));
        }
    }

    @DeleteMapping("/{studentId}/{courseId}")
    public ResponseEntity<?> deleteEnrollment(@PathVariable String studentId, @PathVariable String courseId) {
        if (enrollmentService.deleteEnrollment(studentId, courseId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
