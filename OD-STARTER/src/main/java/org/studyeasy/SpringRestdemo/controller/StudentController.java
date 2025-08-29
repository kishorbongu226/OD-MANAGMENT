package org.studyeasy.SpringRestdemo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.model.Feedback;
import org.studyeasy.SpringRestdemo.model.ODRequest;
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



 
@RestController
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
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/enrollevent/{id}")
    @Operation(summary = "Enroll in an event")
    @ApiResponse(responseCode = "201", description = "Enrollment created")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<?> enroll(
            Authentication authentication,@PathVariable Long id) {
        String username = authentication.getName();
        Optional<Account> accOpt = accountService.findByRegisterNumber(username);
        Optional<Event>  optionalEvent = eventService.findById(id);
        if (optionalEvent.isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            // now you can use eventObj (e.g., eventObj.getName())
        } 
        Event event = optionalEvent.get();
        if(accOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Account student = accOpt.get();

        if(!event.getEligibleYears().contains(student.getAcademicYear()))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("you are not eligible to enroll in this event");
        }

        
        Enrollment enrollment = new Enrollment();
        enrollment.setAccount(accOpt.get());
        enrollment.setEvent(event);
        enrollment.setStatus(RequestStatus.ENROLLED);

        Enrollment saved = enrollmentService.save(enrollment);

        EnrollmentDTO response = new EnrollmentDTO(saved.getId(),saved.getAccount().getRegisterNo(),saved.getEvent().getId(),saved.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // View Student Enrollments
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/myenrollments")
    @Operation(summary = "Get student's enrollments")
    @ApiResponse(responseCode = "200", description = "Enrollments fetched")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<EnrollmentDTO>> myEnrollments(Authentication authentication) {
        String username = authentication.getName();
        Optional<Account> accOpt = accountService.findByRegisterNumber(username);

        if (accOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<EnrollmentDTO> enrollments = enrollmentService.findByAccountId(accOpt.get().getRegisterNo())
                .stream()
                .map(e -> new EnrollmentDTO(e.getId(),e.getAccount().getRegisterNo(), e.getEvent().getId(), e.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrollments);
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

     @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/ODRequest/{id}/submit")
        @SecurityRequirement(name="studyeasy-demo-api")

    public ResponseEntity<ODResponseDTO> submitEvent( Authentication authentication,@PathVariable Long id) {
        String username = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByRegisterNumber(username);
        if(optionalAccount.isEmpty()){
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Enrollment> optionalEnrollment = enrollmentService.findById(id);
        if(optionalEnrollment.isEmpty()){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Enrollment enrollment = optionalEnrollment.get();
        ODRequest odRequest = new ODRequest();
         odRequest.setEnrollment(enrollment);
         odRequest.setStatus(ODStatus.PENDING); // assuming default status is PENDING

        ODRequest odRequest_saved = odRequestService.save(odRequest);

        ODResponseDTO response= new ODResponseDTO(
            odRequest_saved.getId(),odRequest_saved.getEnrollment().getAccount().getRegisterNo(),odRequest_saved.getStatus()
    );

    

      
        return ResponseEntity.ok(response);
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
     @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/UpcomingEvents")
    @Operation(summary = "Get student's approval requests")
    @ApiResponse(responseCode = "200", description = "Approvals fetched")
    @SecurityRequirement(name = "studyeasy-demo-api")
     public ResponseEntity<List<Event>> UpcomingEvents(Authentication authentication){
        return ResponseEntity.ok(eventService.getUpcomingApprovedEvents());

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