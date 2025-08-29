package org.studyeasy.SpringRestdemo.payload.auth;

import org.studyeasy.SpringRestdemo.util.constants.ODStatus;

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
public class ODResponseDTO {
    private Long id;

    private String registerNo;

    private ODStatus status;
}
