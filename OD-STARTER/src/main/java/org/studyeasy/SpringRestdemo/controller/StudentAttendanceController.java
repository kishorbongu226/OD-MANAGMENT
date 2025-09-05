package org.studyeasy.SpringRestdemo.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.service.AccountService;
import org.studyeasy.SpringRestdemo.service.EnrollmentService;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.util.constants.AttendenceStatus;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Controller
@RequestMapping("/api/v1/student")
public class StudentAttendanceController {
private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @Autowired
    private AccountService accountService;

    @Autowired
    private EventService eventService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/events/")
   public String getAllEvents(Model model) {
    List<Event> events = eventService.findAll(); // fetch all events
    model.addAttribute("events", events); // add list to model
    return "scanAttendence"; // points to eventList.html in templates/
}

    // // @PreAuthorize("hasAuthority('STUDENT')")
    // @PostMapping("/attend/{eventKey}")
    // public ResponseEntity<String> markAttendance(@PathVariable String eventKey) {
        

    //     // 1. Find student account
    //     Optional<Account> optionalaccount = accountService.findByRegisterNumber("43111437");
    //         // .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Student not found"));
    //     Account account = optionalaccount.get();
    //     // 2. Find event by eventKey
    //     Event event = eventService.findByEventKey(eventKey)
    //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

    //     // 3. Find enrollment
    //     Enrollment enrollment = enrollmentService.findByAccountAndEvent(account, event)
    //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enrolled in this event"));

    //     // 4. Check time window
    //     LocalDateTime now = LocalDateTime.now();
    //     if (now.isBefore(event.getStartTime()) || now.isAfter(event.getEndTime())) {
    //         return ResponseEntity.badRequest().body("Attendance can only be marked during the event.");
    //     }

    //     // 5. Mark PRESENT
    //     enrollment.setAttendenceStatus(AttendenceStatus.PRESENT);
    //     enrollmentService.save(enrollment);

    //     return ResponseEntity.ok("Attendance marked as PRESENT for event: " + event.getTitle());
    // }

    @PostMapping("/attend/{eventKey}")
    public ResponseEntity<String> markAttendance(@PathVariable String eventKey) {
        logger.info("Received attendance request for eventKey: {}", eventKey);

        // 1. Find student account (for now hardcoded)
        Optional<Account> optionalaccount = accountService.findByRegisterNumber("43111437");
        if (optionalaccount.isEmpty()) {
            logger.warn("Student with registerNumber 43111437 not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Student not found");
        }
        Account account = optionalaccount.get();
        // logger.debug("Student found: {}", account.getId());

        // 2. Find event by eventKey
        Event event = eventService.findByEventKey(eventKey)
            .orElseThrow(() -> {
                logger.error("Event not found for eventKey: {}", eventKey);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
            });
        logger.info("Event found: {} ({})", event.getTitle(), event.getEventKey());

        // 3. Find enrollment
        Enrollment enrollment = enrollmentService.findByAccountAndEvent(account, event)
            .orElseThrow(() -> {
                logger.warn("Enrollment not found for student {} in event {}", 
                            account.getAcademicYear(), event.getEventKey());
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enrolled in this event");
            });
        logger.debug("Enrollment found: {}", enrollment.getId());

        // 4. Check time window
        LocalDateTime now = LocalDateTime.now();
        logger.debug("Current time: {}, Event start: {}, Event end: {}", now, event.getStartTime(), event.getEndTime());

       if (now.isBefore(event.getStartTime()) || now.isAfter(event.getEndTime())) {
    logger.warn("Attendance attempt outside event time window for student {}", enrollment.getId());
    return ResponseEntity.badRequest().body("Attendance can only be marked during the event.");
}


        // 5. Mark PRESENT
        enrollment.setAttendenceStatus(AttendenceStatus.PRESENT);
        enrollmentService.save(enrollment);
        logger.info("Attendance marked PRESENT for student {} in event {}", 
                     event.getEventKey());

        return ResponseEntity.ok("Attendance marked as PRESENT for event: " + event.getTitle());
    }


    @GetMapping("/certificate/{id}")
public ResponseEntity<InputStreamResource> generateCertificate(@PathVariable Long id) {

    // Hardcoded student register number
    Optional<Account> optionalAccount = accountService.findByRegisterNumber("43111437");
    if (optionalAccount.isEmpty()) {
        logger.warn("Student with registerNumber 43111437 not found");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Student not found");
    }
    Account account = optionalAccount.get();

    // Find event by ID
    Event event = eventService.findById(id)
            .orElseThrow(() -> {
                logger.error("Event not found for eventId: {}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
            });
    logger.info("Event found: {} ({})", event.getTitle(), event.getId());

    // Find enrollment for student and event
    Optional<Enrollment> enrollmentOpt = enrollmentService.findByAccountAndEvent(account, event);
    if (enrollmentOpt.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enrollment not found");
    }
    Enrollment enrollment = enrollmentOpt.get();

    // Check attendance
    if (enrollment.getAttendenceStatus() != AttendenceStatus.PRESENT) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attendance not marked yet");
    }

    // Load HTML template from classpath
    String htmlTemplate;
    try {
        ClassPathResource resource = new ClassPathResource("templates/certificate.html");
        try (InputStream inputStream = resource.getInputStream()) {
            htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    } catch (IOException e) {
        logger.error("Failed to load certificate template", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Certificate template not found");
    }

    // Replace placeholders in HTML
    htmlTemplate = htmlTemplate.replace("{{REGISTER_NO}}", account.getRegisterNo())
                               .replace("{{EVENT_TITLE}}", event.getTitle())
                               .replace("{{EVENT_DATE}}", event.getStartTime().toLocalDate().toString());

    // Convert HTML to PDF
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(htmlTemplate, null);
        builder.toStream(baos);
        builder.run();
    } catch (Exception e) {
        logger.error("PDF generation failed", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF");
    }

    // Return PDF as download
    ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=Certificate_" + account.getRegisterNo() + "_" + id + ".pdf");

    return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(bis));
}
}









