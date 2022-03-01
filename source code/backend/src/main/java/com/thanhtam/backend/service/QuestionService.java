package com.thanhtam.backend.service;

import java.util.List;
import java.util.Optional;

import com.thanhtam.backend.entity.Part;
import com.thanhtam.backend.entity.Question;
import com.thanhtam.backend.entity.QuestionType;
import com.thanhtam.backend.repository.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getQuestionList() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public List<Question> findByPart(Part part) {
        return (List<Question>) questionRepository.findByPart(part);
    }

    public Page<Question> findQuestionsByPart(Pageable pageable, Part part) {
        return questionRepository.findQuestionByPart(pageable, part);
    }

    public Page<Question> findAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    public List<Question> getQuestionByQuestionType(QuestionType questionType) {
        return questionRepository.findByQuestionType(questionType);
    }

    public void save(Question question) {

        int point;

        switch (question.getDifficultyLevel()) {
            case EASY:
                point = 5;
                break;

            case MEDIUM:
                point = 10;
                break;

            case HARD:
                point = 15;
                break;

            default:
                point = 0;
                break;
        }

        question.setPoint(point);
        questionRepository.save(question);

    }

    public void delete(Long id) {

        Question question = questionRepository.findById(id).get();

        if (question != null) {
            questionRepository.deleteById(id);
        }

    }

    public Question findById(Long questionId) {
        return questionRepository.findById(questionId).get();
    }
}
