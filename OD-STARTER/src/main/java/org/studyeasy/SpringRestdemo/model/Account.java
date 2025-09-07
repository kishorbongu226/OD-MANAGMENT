package org.studyeasy.SpringRestdemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@ToString(exclude = "coordinator")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "register_no", unique = true)
    private String registerNo;

    private String studentName;

    private long age;

    private String branch;

 @Column(nullable = false)
private Long academicYear = 2025L;

    private String department;

    private String section;

    private String mobile_no;

    private Long events_attended;

    private String email;

    private String password;

    private String Authorities;

    @ManyToOne
    @JoinColumn(name = "coordinator_id", nullable = false) 
    private ProfessorAccount coordinator;
}
