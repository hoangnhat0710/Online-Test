package com.thanhtam.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.thanhtam.backend.dto.AnswerSheet;
import com.thanhtam.backend.dto.ChoiceCorrect;
import com.thanhtam.backend.dto.ChoiceList;
import com.thanhtam.backend.dto.ExamQuestionPoint;
import com.thanhtam.backend.entity.Choice;
import com.thanhtam.backend.entity.Exam;
import com.thanhtam.backend.entity.Question;
import com.thanhtam.backend.entity.User;
import com.thanhtam.backend.repository.ExamRepository;
import com.thanhtam.backend.ultilities.EQTypeCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private PartService partService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    public Page<Exam> findAll(Pageable pageable) {
        return examRepository.findAll(pageable);
    }

    public Page<Exam> findAllByCreated(Pageable pageable, User user) {
        return examRepository.findByCreated_by(pageable, user);
    }

    public Optional<Exam> getById(Long id) {
        return examRepository.findById(id);
    }

    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }

    public List<ChoiceList> getChoiceList(List<AnswerSheet> userChoices) {

        List<ChoiceList> choiceLists = new ArrayList<>();

        userChoices.forEach(item -> {
            ChoiceList choiceList = new ChoiceList();
            Question question = questionService.getQuestionById(item.getQuestionId()).get();
            choiceList.setQuestion(question);
            choiceList.setPoint(item.getPoint());

            Choice choice = choiceService.findById(item.getChoiceId());
            if (choice.getIsCorrected() == 1) {
                choiceList.setIsSelectedCorrected(true);
            } else {
                choiceList.setIsSelectedCorrected(false);

            }

            choiceLists.add(choiceList);

        });

        return choiceLists;

    }

    public void cancelExam(Exam exam) {
           exam.setCanceled(true);
           examRepository.save(exam);
    }
}
