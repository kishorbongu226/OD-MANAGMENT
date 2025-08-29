package org.studyeasy.SpringRestdemo.payload.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProfileDTO {

    private long id;
    private String register_no;

    private long academicYear;

    private long age;

    

    private String branch;

    private String department;

    private String section;

    private String mobile_no;

    private long events_attended;

    private String email;


    private String authorities;

    private String coordinator;
    
}
