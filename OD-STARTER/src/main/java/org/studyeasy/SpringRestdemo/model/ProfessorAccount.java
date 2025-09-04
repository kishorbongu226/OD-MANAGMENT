package org.studyeasy.SpringRestdemo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class ProfessorAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "register_no", unique = true)
    private String registerNo;

    private String name;

    private String designation;

    private String email;

    private String profName;

    private String department;

    private String branch;

    private long age;

    private String password;

    private String Authorities;//ADMIN,TEACHER are the authorities that can be used here for event and teacher management

    @OneToMany(mappedBy = "coordinator")
    private List<Account> students = new ArrayList<>();

    
}
