package org.studyeasy.SpringRestdemo.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.service.EnrollmentService;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.util.constants.AttendenceStatus;

@Component
public class AttendanceScheduler {

    @Autowired
    private EventService eventService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void markAbsentees() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> pastEvents = eventService.findByEndTimeBefore(now);

        for (Event event : pastEvents) {
            List<Enrollment> pendingEnrollments =
                enrollmentService.findByEventAndAttendenceStatus(event, AttendenceStatus.PENDING);

            for (Enrollment e : pendingEnrollments) {
                e.setAttendenceStatus(AttendenceStatus.ABSENT);
            }
            enrollmentService.saveAll(pendingEnrollments);
        }
    }
}