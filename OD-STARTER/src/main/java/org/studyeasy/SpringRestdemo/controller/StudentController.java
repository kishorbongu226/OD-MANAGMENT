package org.studyeasy.SpringRestdemo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.model.Feedback;
import org.studyeasy.SpringRestdemo.model.ODRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.studyeasy.SpringRestdemo.payload.auth.ODResponseDTO;
import org.studyeasy.SpringRestdemo.payload.auth.ProfileDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.ApprovalRequestDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.EnrollmentDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.FeedbackDTO;
import org.studyeasy.SpringRestdemo.service.AccountService;
import org.studyeasy.SpringRestdemo.service.ApprovalService;
import org.studyeasy.SpringRestdemo.service.CertificateService;
import org.studyeasy.SpringRestdemo.service.EnrollmentService;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.service.FeedbackService;
import org.studyeasy.SpringRestdemo.service.ODRequestService;
import org.studyeasy.SpringRestdemo.util.constants.ODStatus;
import org.studyeasy.SpringRestdemo.util.constants.RequestStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;



 
@Controller
@RequestMapping("/api/v1/student")
@Tag(name = "Student Controller", description = "Controller for student operations")
@Slf4j
public class StudentController {

    @Autowired 
    private AccountService accountService;
    @Autowired 
    private EnrollmentService enrollmentService;
    @Autowired 
    private FeedbackService feedbackService;
    @Autowired 
    private ApprovalService approvalService;
    @Autowired 
    private CertificateService certificateService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ODRequestService odRequestService;

    // Get Student Profile
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/profile")
    @Operation(summary = "Get student profile")
    @ApiResponse(responseCode = "200", description = "Profile fetched")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<ProfileDTO> getProfile(Authentication authentication) {
        String username = authentication.getName();
        Optional<Account> accountOpt = accountService.findByRegisterNumber(username);

        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

       Account account = accountOpt.get();
        ProfileDTO profileDTO = new ProfileDTO(
            account.getId(),
            account.getRegisterNo(),
            account.getAcademicYear(),
            account.getAge(),
            account.getBranch(),
            account.getDepartment(),
            account.getSection(),
            account.getMobile_no(),
            account.getEvents_attended(),
            account.getEmail(),
            account.getAuthorities(),
            account.getCoordinator() != null ? account.getCoordinator().getName() : null);

    return ResponseEntity.ok(profileDTO);
    }



    // Enroll in Event
   @PostMapping("/events/enroll/{id}")
    @Operation(summary = "Enroll in an event")
    public String enroll(@PathVariable Long id, Model model) {
        String registerNumber = "43111437"; // hardcoded student
        log.info("Student {} is enrolling in event {}", registerNumber, id);

        Optional<Account> accOpt = accountService.findByRegisterNumber(registerNumber);
        
        Optional<Event> optionalEvent = eventService.findById(id);

        if (optionalEvent.isEmpty()) {
            log.error("Event not found: {}", id);
            model.addAttribute("error", "Event not found!");
            return "error"; // error.html
        }

        if (accOpt.isEmpty()) {
            log.error("Account not found for {}", registerNumber);
            model.addAttribute("error", "Account not found!");
            return "error"; 
        }

        Event event = optionalEvent.get();
        Account student = accOpt.get();

        if (!event.getEligibleYears().contains(student.getAcademicYear())) {
            log.warn("Student {} not eligible for event {}", registerNumber, id);
            model.addAttribute("error", "You are not eligible to enroll in this event.");
            return "error";
        }

        Optional<Enrollment> existingEnrollment = enrollmentService.findByAccountAndEvent(student, event);
        if (existingEnrollment.isPresent()) {
            log.info("Student {} already enrolled in event {}", registerNumber, id);
            model.addAttribute("message", "You are already enrolled in this event.");
            model.addAttribute("event", event);
            return "studentUpcoming"; // thymeleaf page
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setAccount(student);
        enrollment.setEvent(event);
        enrollment.setStatus(RequestStatus.ENROLLED);

        Enrollment saved = enrollmentService.save(enrollment);

        log.info("Enrollment created successfully for student {} in event {}", registerNumber, id);

        model.addAttribute("message", "Enrollment successful!");
        model.addAttribute("enrollmentId", saved.getId());
        model.addAttribute("event", event);
        model.addAttribute("student", student);

        return "studentUpcoming"; // thymeleaf page
    }

@GetMapping("/events/myenrollments")
@Operation(summary = "Get student's enrollments")
@SecurityRequirement(name = "studyeasy-demo-api")
public String myEnrollments(Model model) {
    String username = "43111437";
    log.info("Fetching enrollments for student: {}", username);

    Optional<Account> accOpt = accountService.findByRegisterNumber(username);

    if (accOpt.isEmpty()) {
        log.warn("Account not found for student: {}", username);
        model.addAttribute("error", "Account not found!");
        return "error"; // error.html
    }

    Account student = accOpt.get();

    // Directly fetch Enrollment entities
    List<Enrollment> enrollments = enrollmentService.findByRegisterNo(student.getRegisterNo());

    log.info("Found {} enrollments for student {}", enrollments.size(), username);

    model.addAttribute("enrollments", enrollments);
    model.addAttribute("student", student);

    return "studentApproval"; // thymeleaf page: studentEnrollments.html
}



    // Submit Feedback
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/givefeedback")
    @Operation(summary = "Submit feedback for an event")
    @ApiResponse(responseCode = "201", description = "Feedback submitted")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<FeedbackDTO> giveFeedback(
            Authentication authentication,
            @Valid @RequestBody FeedbackDTO dto) {
        Feedback feedback = new Feedback();
        feedback.setEventId(dto.getEventId());
        feedback.setComments(dto.getComments());
        feedback.setRating(dto.getRating());

        Feedback saved = feedbackService.save(feedback);

        FeedbackDTO response = new FeedbackDTO(saved.getId(),saved.getRegisterNo(), saved.getEventId(), saved.getComments(), saved.getRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/ODRequest/{id}/submit")
@SecurityRequirement(name = "studyeasy-demo-api")
public String requestOd(@PathVariable Long id, Model model) {
    

    String username = "43111437";
    log.info("OD Request initiated for student registerNo={} and enrollementId={}", username, id);

    // ✅ Verify account
    Optional<Account> optionalAccount = accountService.findByRegisterNumber(username);
    if (optionalAccount.isEmpty()) {
        log.warn("Account not found for registerNo={}", username);
        model.addAttribute("error", "Account not found for user: " + username);
        return "error";
    }

    // ✅ Verify enrollment
    Optional<Enrollment> optionalEnrollment = enrollmentService.findById(id);
    if (optionalEnrollment.isEmpty()) {
        log.warn("Enrollment not found for id={} by student={}", id, username);
        model.addAttribute("error", "Enrollment not found with ID: " + id);
        return "error";
    }

    Enrollment enrollment = optionalEnrollment.get();
    log.debug("Fetched enrollement id={} for student={}", enrollment.getId(), username);

    // ✅ Check if already requested
    if (odRequestService.existsByEnrollment(enrollment)) {
        log.info("OD Request already exists for enrollementId={} student={}", enrollment.getId(), username);
        model.addAttribute("message", "OD Request already submitted for this enrollment.");
        model.addAttribute("student", optionalAccount.get());
        return "studentApproval";
    }

    // ✅ Create OD Request
    ODRequest odRequest = new ODRequest();
    odRequest.setEnrollment(enrollment);
    odRequest.setStatus(ODStatus.PENDING);
    odRequestService.save(odRequest);

    log.info("New OD Request created with status=PENDING for enrollmentId={} student={}", enrollment.getId(), username);

    // ✅ Add success message & data to the model
    model.addAttribute("message", "OD Request submitted successfully.");
    model.addAttribute("student", optionalAccount.get());
    model.addAttribute("enrollments",
        enrollmentService.findByRegisterNo(optionalAccount.get().getRegisterNo()));

    log.debug("Reloaded enrollments for student={} after OD request", username);

    return "studentApproval";
}



    // View Approvals
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("myapprovals")
    @Operation(summary = "Get student's approval requests")
    @ApiResponse(responseCode = "200", description = "Approvals fetched")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<ApprovalRequestDTO>> myApprovals(Authentication authentication) {
        String username = authentication.getName();
        Optional<Account> accOpt = accountService.findByRegisterNumber(username);

        if (accOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ApprovalRequestDTO> approvals = approvalService.findByAccountId(accOpt.get().getRegisterNo())
                .stream()
                .map(a -> new ApprovalRequestDTO(a.getId(), a.getRegisterNo(), a.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(approvals);
    }

    // View Certificates
    // @PreAuthorize("hasAuthority('STUDENT')")
    // @GetMapping("/certificates")
    // @Operation(summary = "Get student's certificates")
    // @ApiResponse(responseCode = "200", description = "Certificates fetched")
    // @SecurityRequirement(name = "studyeasy-demo-api")
    // public ResponseEntity<List<CertificateDTO>> myCertificates(Authentication authentication) {
    //     String username = authentication.getName();
    //     Optional<Account> accOpt = accountService.findByRegisterNumber(username);

    //     if (accOpt.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //     }

    //     List<CertificateDTO> certs = certificateService.findByRegisterId(accOpt.get().getRegisterNo())
    //             .stream()
    //             .map(c -> new CertificateDTO(c.getId(), c.getCertificateUrl(), c.getIssuedDate()))
    //             .collect(Collectors.toList());

    //     return ResponseEntity.ok(certs);
    // }
     @GetMapping("/events/upcoming")
     public String getUpcomingevents(Model model){

    List<Event> events = eventService.findAll();
     model.addAttribute("events",events);
     model.addAttribute("event", new Event());    
        return "studentUpcoming";
    }


     @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/OngoingEvents")
    @Operation(summary = "Get student's approval requests")
    @ApiResponse(responseCode = "200", description = "Approvals fetched")
    @SecurityRequirement(name = "studyeasy-demo-api")
     public ResponseEntity<List<Event>> OngoingEvents(Authentication authentication){
        return ResponseEntity.ok(eventService.getOngoingApprovedEvents());

     }
}