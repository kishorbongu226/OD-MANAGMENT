package org.studyeasy.SpringRestdemo.model;

import java.time.LocalDateTime;
import java.util.List;

import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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

    private String type;

    private String title;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)

    private List<String> eventCordinator;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    
  

    @Enumerated(EnumType.STRING)
    private EventStatus status;   // PENDING, APPROVED, DECLINED,COMPLETED

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> eligibleYears;

    private String createdBy;
        private String eventKey;

    @PrePersist
    public void generateEventKey() {
        this.eventKey = "EVT-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }
    public String getStatusClass() {
    switch (status) {
        case APPROVED: return "status-approved";
        case DECLINED: return "status-declined";
        default: return "status-pending";
    }
}

}
