package com.thanhtam.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.thanhtam.backend.entity.Exam;
import com.thanhtam.backend.entity.ExamUser;
import com.thanhtam.backend.entity.User;
import com.thanhtam.backend.repository.ExamRepository;
import com.thanhtam.backend.repository.ExamUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamUserService {

    @Autowired
    private ExamUserRepository examUserRepository;

    @Autowired
    private ExamRepository examRepository;

    public List<ExamUser> getExamListByUser(User user) {
        return examUserRepository.findByUser(user);
    }

    public Optional<ExamUser> findByExamUserById(Long examId) {
        return examUserRepository.findById(examId);
    }

    public void create(Exam exam, List<User> users) {

        List<ExamUser> examUsers = new ArrayList<>();

        users.forEach(user -> {
            ExamUser examUser = new ExamUser();
            examUser.setExam(exam);
            examUser.setUser(user);
            examUsers.add(examUser);
        });

        examUserRepository.saveAll(examUsers);

    }

    public ExamUser findByExamAndUser(Long examId, Long userId) {
        return examUserRepository.findByExam_IdAndUser_Id(examId, userId);
    }

    public void update(ExamUser examUser) {

        examUserRepository.save(examUser);
    }

}
