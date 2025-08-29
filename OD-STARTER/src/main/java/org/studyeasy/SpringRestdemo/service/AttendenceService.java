package org.studyeasy.SpringRestdemo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestdemo.model.AttendenceQR;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.repository.AttendenceQRRepository;
import org.studyeasy.SpringRestdemo.repository.EnrollmentRepository;
import org.studyeasy.SpringRestdemo.repository.EventRepository;
import org.studyeasy.SpringRestdemo.util.constants.AttendenceStatus;

@Service
public class AttendenceService {

    @Autowired private EventRepository eventRepo;
    @Autowired private AttendenceQRRepository qrRepo;
    @Autowired private EnrollmentRepository enrollmentRepo;

    // Coordinator generates QR
    public AttendenceQR generateQR(Long eventId, int minutes) {
        AttendenceQR qr = new AttendenceQR();
        Event event = eventRepo.findById(eventId)
        .orElseThrow(() -> new RuntimeException("Event not found"));
        qr.setEvent(event);
        qr.setToken(UUID.randomUUID().toString());
        qr.setGeneratedAt(LocalDateTime.now());
        qr.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));
        qr.setActive(true);
        return qrRepo.save(qr);
    }

    // Student scans QR
    public String markAttendance(String token, String registerNo) {
        AttendenceQR qr = qrRepo.findByTokenAndActiveTrue(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired QR"));

        if (qr.getExpiresAt().isBefore(LocalDateTime.now())) {
            qr.setActive(false);
            qrRepo.save(qr);
            throw new RuntimeException("QR expired");
        }

        Enrollment enrollment = enrollmentRepo.findByEvent_IdAndAccount_RegisterNo(qr.getEvent().getId(), registerNo).orElseThrow(() -> new RuntimeException("Student not enrolled for this event"));


        enrollment.setAttendenceStatus(AttendenceStatus.PRESENT);
        enrollmentRepo.save(enrollment);

        return "Attendance marked successfully!";
    }

    // Coordinator views enrolled students with attendance
    public List<Enrollment> getEventAttendance(Long eventId) {
        return enrollmentRepo.findByEvent_Id(eventId);
    }
}


