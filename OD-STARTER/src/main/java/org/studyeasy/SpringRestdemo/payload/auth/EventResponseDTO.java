package org.studyeasy.SpringRestdemo.payload.auth;

import java.time.LocalDateTime;
import java.util.List;

import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> eventCordinator;
    private List<Long> eligibleYears;
     @Enumerated(EnumType.STRING)
    private EventStatus status;  
}

