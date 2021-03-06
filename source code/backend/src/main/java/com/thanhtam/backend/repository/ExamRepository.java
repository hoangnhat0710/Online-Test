package com.thanhtam.backend.repository;

import com.thanhtam.backend.entity.Exam;
import com.thanhtam.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

   Page<Exam> findByCreated_by(Pageable pageable, User created_by);

}
