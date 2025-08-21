package org.studyeasy.SpringRestdemo.model;

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
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

   @Column(name = "register_no", unique = true)
    private String registerNo;
    
    private Long enrollmentId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;    // PENDING, APPROVED, DECLINED
}
