package org.studyeasy.SpringRestdemo.payload.auth.student;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
      private String registerNo;

     private long age;

    private String co_ordinator;

    private String branch;

    private long academicYear;

    private String department;

    private String section;

    private String mobile_no;

    private long events_attended;
}

