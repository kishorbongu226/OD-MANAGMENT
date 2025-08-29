package org.studyeasy.SpringRestdemo.payload.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessorProfileDTO {
    private Long id;
    private String registerNo;
    private String name;
    private String designation;
    private String email;
    private String department;
    private String branch;
    private Long age;
}
