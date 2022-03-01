package com.thanhtam.backend.repository;

import com.thanhtam.backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findByCourseCode(String courseCode);

    @Query(value = "SELECT * FROM course JOIN part ON course.id = part.course_id WHERE part.course_id = ?1", nativeQuery = true)
    Course findCourseByPartId(Long partId);

  

}

