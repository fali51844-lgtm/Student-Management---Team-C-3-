package com.studentmgmt.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentmgmt.dto.StudentDTO;
import com.studentmgmt.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)  // Make sure StudentController is in the same package
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllStudents() throws Exception {
        StudentDTO student1 = new StudentDTO();
        student1.setId("1");
        student1.setFirstName("John");
        student1.setLastName("Doe");

        StudentDTO student2 = new StudentDTO();
        student2.setId("2");
        student2.setFirstName("Jane");
        student2.setLastName("Smith");

        when(studentService.getAllStudentsWithDetails()).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void testGetStudentById() throws Exception {
        String studentId = "1";
        StudentDTO student = new StudentDTO();
        student.setId(studentId);
        student.setFirstName("John");
        student.setLastName("Doe");

        when(studentService.getStudentById(studentId)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testCreateStudent() throws Exception {
        StudentDTO inputStudent = new StudentDTO();
        inputStudent.setFirstName("John");
        inputStudent.setLastName("Doe");
        inputStudent.setEmail("john.doe@example.com");

        StudentDTO createdStudent = new StudentDTO();
        createdStudent.setId("1");
        createdStudent.setStudentId("STU001");
        createdStudent.setFirstName("John");
        createdStudent.setLastName("Doe");
        createdStudent.setEmail("john.doe@example.com");

        when(studentService.getStudentByEmail(any())).thenReturn(Optional.empty());
        when(studentService.createStudent(any())).thenReturn(createdStudent);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.studentId").value("STU001"));
    }

    @Test
    void testDeleteStudent() throws Exception {
        String studentId = "1";
        when(studentService.deleteStudent(studentId)).thenReturn(true);

        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isNoContent());
    }
}
    

