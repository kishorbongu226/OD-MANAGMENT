package org.studyeasy.SpringRestdemo.model;

import java.time.LocalDateTime;
import java.util.List;

import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> eventCordinator;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

  

    @Enumerated(EnumType.STRING)
    private EventStatus status;   // PENDING, APPROVED, DECLINED

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> eligibleYears;

    private String createdBy;
}
