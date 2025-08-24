package org.studyeasy.SpringRestdemo.payload.auth.student;


import org.studyeasy.SpringRestdemo.util.constants.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRequestDTO {
    private Long id;
    private String registerNo;
    // private String requestType; // e.g., "Leave", "Certificate"
    // private String reason;
    private RequestStatus status;  // PENDING / APPROVED / DECLINED
}


