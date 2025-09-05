package org.studyeasy.SpringRestdemo.model;

import org.studyeasy.SpringRestdemo.util.constants.ODStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class ODRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    

    @OneToOne
    @JoinColumn(name="enrollement_id", referencedColumnName = "enrollement_id", nullable = false)
    private Enrollment enrollment;

    private String approver;

    @Enumerated(EnumType.STRING)
    private ODStatus status;

    @OneToOne
    @JoinColumn(name="event_id", referencedColumnName="id", nullable=true)
    private Event event;

    
}
