package org.studyeasy.SpringRestdemo.model;

import java.time.LocalDateTime;

import org.studyeasy.SpringRestdemo.util.constants.AttendenceStatus;
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

   
    @ManyToOne
    @JoinColumn(name="event_id",referencedColumnName = "id", nullable = false)
    private Event event;   // FK → Event

    private LocalDateTime enrolledAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RequestStatus status;    // REQUESTED, APPROVED, DECLINED

    @Enumerated(EnumType.STRING)
    private AttendenceStatus attendenceStatus = AttendenceStatus.PENDING;

    @ManyToOne
    @JoinColumn(name="register_id", referencedColumnName = "register_no", nullable = false)
    private Account account;
}
