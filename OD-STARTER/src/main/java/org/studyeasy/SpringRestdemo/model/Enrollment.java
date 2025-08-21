package org.studyeasy.SpringRestdemo.model;

import java.time.LocalDateTime;

import org.studyeasy.SpringRestdemo.util.constants.RequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "register_no", unique = true)
    private String registerNo;
    
    private Long eventId;   // FK → Event

    private LocalDateTime enrolledAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RequestStatus status;    // REQUESTED, APPROVED, DECLINED
}
