package org.studyeasy.SpringRestdemo.payload.auth.student;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {

    private String registerNo;
    private int age;
    private String branch;
    private String department;
    private String section;
    private String mobileNo;
    private String email;
    private String academicYear;
    private int eventsAttended;
}

