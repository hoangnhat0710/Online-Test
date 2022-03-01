package com.thanhtam.backend.repository;

import com.thanhtam.backend.entity.Exam;
import com.thanhtam.backend.entity.ExamUser;
import com.thanhtam.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamUserRepository extends JpaRepository<ExamUser, Long> {

    List<ExamUser> findByUser(User user);

    ExamUser findByExam_IdAndUser_Id(Long examId, Long userId);

}
