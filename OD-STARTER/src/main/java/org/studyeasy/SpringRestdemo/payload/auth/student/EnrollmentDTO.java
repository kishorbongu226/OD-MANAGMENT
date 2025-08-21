package org.studyeasy.SpringRestdemo.payload.auth.student;

import org.studyeasy.SpringRestdemo.util.constants.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private String registerNo;
    private Long eventId;
    private RequestStatus status;  // PENDING / APPROVED / DECLINED
}

