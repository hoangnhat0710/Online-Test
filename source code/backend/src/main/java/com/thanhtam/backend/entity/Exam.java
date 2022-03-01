package com.thanhtam.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thanhtam.backend.audit.Auditable;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "exam")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "canceled")
    private boolean canceled = false;

    @ManyToOne()
    @JoinColumn(name = "intake_id")
    private Intake intake;

    @ManyToOne()
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "title")
    private String title;

    @Column(name = "begin_exam")
    private Date beginExam;

    @Column(name = "finish_exam")
    private Date finishExam;

    @Transient
    private boolean locked;

    @Column(name="question_data", columnDefinition = "text")
    private String questionData;


}
