package org.studyeasy.SpringRestdemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "register_no", unique = true)
    private String registerNo;


    private long age;

    private String co_ordinator;

    private String branch;

    private long academicYear;

    private String department;

    private String section;

    private String mobile_no;

    private long events_attended;

    private String email;

    private String password;

    private String Authorities;
}
