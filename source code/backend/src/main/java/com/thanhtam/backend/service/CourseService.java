package com.thanhtam.backend.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.thanhtam.backend.entity.Course;
import com.thanhtam.backend.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getCourseList() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public boolean existsByCode(String courseCode) {

        Course course = courseRepository.findByCourseCode(courseCode);
        if (course == null) {
            return false;
        }

        return true;

    }

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    public Course findCourseByPartId(Long partId) {
        return courseRepository.findCourseByPartId(partId);
    }
}
