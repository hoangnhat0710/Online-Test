package com.thanhtam.backend.controller;

import com.amazonaws.auth.policy.Principal;
import com.amazonaws.services.outposts.model.NotFoundException;
import com.amazonaws.services.schemas.model.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.mysql.fabric.xmlrpc.base.Data;
import com.thanhtam.backend.dto.*;
import com.thanhtam.backend.entity.*;
import com.thanhtam.backend.service.*;
import com.thanhtam.backend.ultilities.ERole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private PartService partService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExamUserService examUserService;

    @Autowired
    private IntakeService intakeService;

    @GetMapping(value = "/exams")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public PageResult getExamsByPage(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        User user = userService.getUserByUsername(username);

        boolean isAdmin = user.getRoles().contains(ERole.ROLE_ADMIN);

        Page<Exam> examPage;

        if (isAdmin) {
            examPage = examService.findAll(pageable);
            return new PageResult(examPage);
        }

        examPage = examService.findAllByCreated(pageable, user);

        return new PageResult(examPage);

    }

    @GetMapping(value = "/exams/list-all-by-user")
    public ResponseEntity<List<ExamUser>> getAllByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.getUserByUsername(username);

        List<ExamUser> examUserList = examUserService.getExamListByUser(user);

        Date currentDate = new Date();

        examUserList.forEach(examUser -> {
            if (currentDate.compareTo(examUser.getExam().getBeginExam()) < 0) {
                examUser.getExam().setLocked(false);
            } else {
                examUser.getExam().setLocked(true);
            }
        });

        return new ResponseEntity(examUserList, HttpStatus.OK);

    }

    @GetMapping(value = "/exams/exam-user/{examId}")
    public ResponseEntity<ExamUser> getExamUserById(@PathVariable Long examId) {

        Optional<ExamUser> examUser = examUserService.findByExamUserById(examId);
        if (!examUser.isPresent()) {
            return new ResponseEntity("Không tìm thấy exam user này", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(examUser.get());
    }

    @GetMapping(value = "/exams/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable("id") Long id) {
        Optional<Exam> exam = examService.getById(id);
        if (!exam.isPresent()) {
            return new ResponseEntity<>(exam.get(),
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(exam.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/exams")
    public ResponseEntity<?> createExam(@Valid @RequestBody Exam exam, @RequestParam Long intakeId,
            @RequestParam Long partId, boolean locked) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Intake> intake = intakeService.findById(intakeId);

        if (intake.isPresent()) {
            exam.setIntake(intake.get());
        }

        User user = userService.getUserByUsername(username);

        Part part = partService.findPartById(partId).get();

        exam.setCreatedBy(user);
        exam.setPart(part);

        examService.save(exam);

        List<User> users = userService.findAllByIntakeId(intakeId);
        examUserService.create(exam, users);

        return ResponseEntity.ok(exam);

    }

    @PutMapping(value = "/exams/{examId}/questions-by-user")
    public void saveUserExamAnswer( Authentication authentication ,@RequestBody List<AnswerSheet> answerSheets, @PathVariable Long examId,
            @RequestParam boolean isFinish, @RequestParam int remainingTime) {

        User user = (User) authentication.getPrincipal();

        Optional<ExamUser> examUser = Optional.ofNullable(examUserService.findByExamAndUser(examId, user.getId());

        if(!examUser.isPresent()) {
            throw new NotFoundException("Not found exam");
        }else {
            if(examUser.get().getIsFinished()) {
                throw new BadRequestException("This exam was end");
            }

          ObjectMapper mapper = new ObjectMapper();

          String answerSheetConvertToJson = mapper.writeValueAsString(answerSheets);
          examUser.get().setAnswerSheet(answerSheetConvertToJson);
          examUser.get().setIsFinished(isFinish);
          if(isFinish == true) {
              examUser.get().setTimeFinish(new Date());
          }

          examUserService.update(examUser.get());


        }


    }

    @GetMapping(value = "/exams/{examId}/result")
    public ResponseEntity getResultExam(@PathVariable Long examId, Authentication authentication) throws IOException {

        ExamResult examResult = new ExamResult();
        User user = (User) authentication.getPrincipal();
        Optional<Exam> exam = examService.getById(examId);
        if (exam.isEmpty()) {
            return new ResponseEntity("Không tìm thấy exam", HttpStatus.NOT_FOUND);
        }

        examResult.setExam(exam.get());
        ExamUser examUser = examUserService.findByExamAndUser(examId, user.getId());
        List<AnswerSheet> userChoices = convertAnswerJsonToObject(examUser);
        List<ChoiceList> choiceLists = examService.getChoiceList(userChoices);
        Double totalPoint = 0.0;
        choiceLists.forEach(choice -> {
            if (choice.getIsSelectedCorrected().equals(true)) {
                totalPoint += choice.getPoint();
            }
        });
        examResult.setTotalPoint(totalPoint);

        return new ResponseEntity(examResult, HttpStatus.OK);

    }

    public List<AnswerSheet> convertAnswerJsonToObject(ExamUser examUser) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
    
        if(Strings.isNullOrEmpty(examUser.getAnswerSheet()) {
            return Collections.emptyList();
        }
        String answerSheet = examUser.getAnswerSheet();
        List<AnswerSheet> choiceUsers = mapper.readValue(answerSheet, new TypeReference<List<AnswerSheet>>() {
        });
        return choiceUsers;
    }

    public List<ExamQuestionPoint> convertQuestionJsonToObject(Exam exam) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        String answerSheet = exam.getQuestionData();
        List<ExamQuestionPoint> questions = mapper.readValue(answerSheet, new TypeReference<List<AnswerSheet>>() {
        });
        return questions;
    }

    @GetMapping(value = "/exam/{id}/question-text")
    public ResponseEntity<?> getQuestionTextByExamId(@PathVariable Long id) throws IOException {
        Optional<Exam> exam = examService.getById(id);
        if (exam.isEmpty()) {
            return new ResponseEntity("Không tìm thấy exam", HttpStatus.NOT_FOUND);

        }

        List<ExamQuestionPoint> questions = convertQuestionJsonToObject(exam.get());
        List<ExamDetail> responses = new ArrayList<>();
        questions.forEach(examQuestionPoint -> {
            ExamDetail examDetail = new ExamDetail();
            Question question = questionService.findById(examQuestionPoint.getQuestionId());
            examDetail.setPoint(examQuestionPoint.getPoint());
            examDetail.setDifficultyLevel(question.getDifficultyLevel().toString());
            examDetail.setQuestionText(question.getQuestionText());
            examDetail.setQuestionType(question.getQuestionType().toString());
            responses.add(examDetail);
        });

        return ResponseEntity.ok(responses);
    }

    @GetMapping(value = "/exams/{id}/cancel")
    public ResponseEntity<?> cancelExam(@PathVariable Long id) {

        Optional<Exam> exam = examService.getById(id);
        if (exam.isEmpty()) {
            return new ResponseEntity("Không tìm thấy exam", HttpStatus.NOT_FOUND);

        }

        Date now = new Date();
        if (exam.get().getBeginExam().compareTo(now) < 0) {
            return new ResponseEntity("Exam was closed", HttpStatus.BAD_REQUEST);

        } else {
            examService.cancelExam(exam.get());
            return new ResponseEntity<ResponseMessage>(new ResponseMessage("Cancel success"), HttpStatus.OK);
        }

    }

}
