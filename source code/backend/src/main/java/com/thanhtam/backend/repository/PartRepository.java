package com.thanhtam.backend.repository;

import com.thanhtam.backend.entity.Course;
import com.thanhtam.backend.entity.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    @Query("SELECT p FROM Part p WHERE p.course_id = ?1")
    Page<Part> findByCourseId(Long courseId, Pageable pageable);

}
