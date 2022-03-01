package com.thanhtam.backend.service;

import java.util.List;
import java.util.Optional;

import com.thanhtam.backend.entity.QuestionType;
import com.thanhtam.backend.repository.QuestionTypeRepository;
import com.thanhtam.backend.ultilities.EQTypeCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionTypeService {

    @Autowired
    private QuestionTypeRepository questionTypeRepository;

    public List<QuestionType> getQuestionTypeList() {
        return questionTypeRepository.findAll();
    }

    public Optional<QuestionType> getQuestionTypeById(Long id) {
        return questionTypeRepository.findById(id);
    }

    public QuestionType getQuestionTypeByCode(EQTypeCode eqTypeCode) {
        return questionTypeRepository.findByTypeCode(eqTypeCode);
    }

    public boolean existsById(Long typeId) {

        QuestionType questionType = questionTypeRepository.findById(typeId).get();

        if (questionType == null) {
            return false;
        }

        return true;
    }

}
