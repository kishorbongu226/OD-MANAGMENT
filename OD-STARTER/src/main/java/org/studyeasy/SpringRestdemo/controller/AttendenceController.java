package org.studyeasy.SpringRestdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestdemo.service.AttendenceService;

@RestController
@RequestMapping("/api/v1/attendence")
public class AttendenceController {

    @Autowired private AttendenceService attendenceService;

    // Student scans QR
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/scan/{token}")
    public ResponseEntity<String> scanQR(Authentication authentication, @PathVariable String token) {
        String registerNo = authentication.getName();
        return ResponseEntity.ok(attendenceService.markAttendance(token, registerNo));
    }
}

