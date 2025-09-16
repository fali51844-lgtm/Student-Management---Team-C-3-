package com.studentmgmt.config;

import com.studentmgmt.entity.Course;
import com.studentmgmt.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() == 0) {
            List<Course> sampleCourses = Arrays.asList(
                    new Course("CS101", "Computer Science 101", "Dr. Sarah Wilson", 300, 3),
                    new Course("MATH201", "Mathematics 201", "Prof. Michael Brown", 200, 4),
                    new Course("ENG102", "English Literature 102", "Dr. Emily Davis", 250, 3)
            );
            
            sampleCourses.forEach(course -> {
                course.setDescription(course.getName().contains("Computer") ? "Introduction to Computer Science" :
                        course.getName().contains("Math") ? "Advanced Mathematics" : "Modern English Literature");
                course.setIsActive(true);
            });
            
            courseRepository.saveAll(sampleCourses);
        }
    }
}