package org.studyeasy.SpringRestdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestdemo.model.AttendenceQR;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.service.AttendenceService;

@RestController
@RequestMapping("/api/v1/coordinator")
public class CoordinatorController {

    @Autowired private AttendenceService attendanceService;

    // Generate QR for an event
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/events/{eventId}/generate-qr")
    public ResponseEntity<AttendenceQR> generateQR(@PathVariable Long eventId) {
        AttendenceQR qr = attendanceService.generateQR(eventId, 15);
        return ResponseEntity.ok(qr);
    }

    // View enrolled students with attendance
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/events/{eventId}/attendance")
    public ResponseEntity<List<Enrollment>> viewAttendance(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendanceService.getEventAttendance(eventId));
    }
}

