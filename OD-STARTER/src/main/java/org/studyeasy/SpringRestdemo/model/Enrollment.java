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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @Column(name = "enrollement_id", unique = true)

    private Long id;

   
    
    private Long eventId;   // FK → Event

    private LocalDateTime enrolledAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RequestStatus status;    // REQUESTED, APPROVED, DECLINED

     @ManyToOne
    @JoinColumn(name="register_id", referencedColumnName = "register_no", nullable = false)
    private Account account;
}
