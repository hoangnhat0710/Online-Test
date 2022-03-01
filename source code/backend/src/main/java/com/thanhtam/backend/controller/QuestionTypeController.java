package com.thanhtam.backend.controller;

import com.thanhtam.backend.dto.ServiceResult;
import com.thanhtam.backend.entity.QuestionType;
import com.thanhtam.backend.service.QuestionTypeService;
import com.thanhtam.backend.ultilities.EQTypeCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api")
@RestController
public class QuestionTypeController {

    @Autowired
    private QuestionTypeService questionTypeService;

    @GetMapping(value = "/question-types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")

    public List<QuestionType> getAllQuestionType() {
        List<QuestionType> questionTypeList = questionTypeService.getQuestionTypeList();

        return questionTypeList;

    }

    @GetMapping(value = "/question-types/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")

    public QuestionType getQuestionTypeById(@PathVariable Long id) {
        QuestionType questionType = questionTypeService.getQuestionTypeById(id).get();
        return questionType;
    }

    @GetMapping(value = "/question-types/{typeCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")

    public QuestionType getQuestionTypeByTypeCode(@PathVariable String typeCode) {
        EQTypeCode eqTypeCode = EQTypeCode.valueOf(typeCode);
        QuestionType questionType = questionTypeService.getQuestionTypeByCode(eqTypeCode);
        return questionType;
    }

    
}
