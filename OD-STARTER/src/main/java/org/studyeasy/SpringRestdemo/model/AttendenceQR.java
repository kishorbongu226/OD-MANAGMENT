package org.studyeasy.SpringRestdemo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AttendenceQR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token; 
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name="event_id", nullable = false)
    private Event event;

    private boolean active = true;
}

