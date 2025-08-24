package org.studyeasy.SpringRestdemo.model;

import java.time.LocalDateTime;

import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

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
@Getter
@Setter
@ToString
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    // @ManyToMany
    // @JoinColumn(name="register_id", referencedColumnName = "register_no", nullable = false)
    // private Account account; 

    @Enumerated(EnumType.STRING)
    private EventStatus status;   // PENDING, APPROVED, DECLINED

    private String createdBy;
}
