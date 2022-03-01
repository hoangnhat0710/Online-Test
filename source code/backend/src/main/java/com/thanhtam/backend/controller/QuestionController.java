package com.thanhtam.backend.controller;

import com.thanhtam.backend.dto.PageResult;
import com.thanhtam.backend.dto.ServiceResult;
import com.thanhtam.backend.entity.*;
import com.thanhtam.backend.service.*;
import com.thanhtam.backend.ultilities.EQTypeCode;
import com.thanhtam.backend.ultilities.ERole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api")
@RestController
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private PartService partService;

    @Autowired
    private QuestionTypeService questionTypeService;

        @GetMapping(value = "/questions")
        @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")

        public ResponseEntity<ServiceResult> getAllQuestion() {
            List<Question> questionList = questionService.getQuestionList();

            return ResponseEntity.ok()
                    .body(new ServiceResult(HttpStatus.OK.value(), "Get question bank successfully!", questionList));
        }

    @GetMapping(value = "/questions/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")

    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        Optional<Question> questionOptional = questionService.getQuestionById(id);
        if (!questionOptional.isPresent()) {
            return ResponseEntity.ok()
                    .body(new ServiceResult(HttpStatus.NOT_FOUND.value(), "Not found with id: " + id, null));
        }
        return ResponseEntity.ok().body(questionOptional.get());
    }

    // Get list of question by part
    @GetMapping(value = "/parts/{partId}/questions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ResponseEntity<?> getQuestionsByPart(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
            @PathVariable Long partId) {

        Part part = partService.findPartById(partId).get();

        if (partId == null) {
            Page<Question> questions = questionService.findAllQuestions(pageable);
            List<Question> list = questions.getContent();

            return new ResponseEntity<List<Question>>(list, HttpStatus.OK);

        } else {

            Page<Question> questions = questionService.findQuestionsByPart(pageable, part);
            List<Question> list = questions.getContent();

            return new ResponseEntity<List<Question>>(list, HttpStatus.OK);

        }

    }

    @GetMapping(value = "/question-types/{typeId}/questions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")

    public ResponseEntity<?> getQuestionByQuestionType(@PathVariable Long typeId) {
        if (questionTypeService.existsById(typeId)) {

            QuestionType questionType = questionTypeService.getQuestionTypeById(typeId).get();
            List<Question> questionList = questionService.getQuestionByQuestionType(questionType);
            return ResponseEntity.ok().body(new ServiceResult(HttpStatus.OK.value(),
                    "Get question list with question type id: " + typeId, questionList));
        }
        return ResponseEntity.ok().body(
                new ServiceResult(HttpStatus.NOT_FOUND.value(), "Not found question type with id: " + typeId, null));
    }

    @PostMapping(value = "/questions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ResponseEntity<?> createQuestion(@RequestBody Question question, @RequestParam String questionType,
            @RequestParam Long partId) {

        EQTypeCode eqTypeCode = EQTypeCode.valueOf(questionType);

        QuestionType questionType2 = questionTypeService.getQuestionTypeByCode(eqTypeCode);

        Part part = partService.findPartById(partId).get();

        question.setQuestionType(questionType);
        question.setPart(part);
            questionService.save(question);

            return (ResponseEntity<?>) ResponseEntity.ok();

    }

    @PutMapping(value = "/questions/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")

    public ResponseEntity<?> updateQuestion(@RequestBody Question question, @PathVariable Long id) {
        Optional<Question> questionReq = questionService.getQuestionById(id);
        if (!questionReq.isPresent()) {
            return ResponseEntity.ok()
                    .body(new ServiceResult(HttpStatus.NOT_FOUND.value(), "Not found with id: " + id, null));
        }
        question.setId(id);
        questionService.save(question);
        return ResponseEntity.ok()
                .body(new ServiceResult(HttpStatus.OK.value(), "Get question with id: " + id, question));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    @GetMapping(value = "/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {

        questionService.delete(id);

        return (ResponseEntity<?>) ResponseEntity.ok();

    }

}
