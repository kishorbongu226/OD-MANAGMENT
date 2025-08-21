package org.studyeasy.SpringRestdemo.payload.auth.student;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    private Long id;
    private String registerNo;
    private Long eventId;
    private String comments;
    private int rating; // 1–5
}
