package org.studyeasy.SpringRestdemo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Feedback;
import org.studyeasy.SpringRestdemo.model.Certificate;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.ApprovalRequest;
import org.studyeasy.SpringRestdemo.payload.auth.student.EnrollmentDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.FeedbackDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.StudentProfileDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.ApprovalRequestDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.CertificateDTO;

import org.studyeasy.SpringRestdemo.service.EnrollmentService;
import org.studyeasy.SpringRestdemo.service.FeedbackService;
import org.studyeasy.SpringRestdemo.service.AccountService;

import org.studyeasy.SpringRestdemo.service.CertificateService;
import org.studyeasy.SpringRestdemo.service.ApprovalService;

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

    // ✅ Get Student Profile
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/profile")
    @Operation(summary = "Get student profile")
    @ApiResponse(responseCode = "200", description = "Profile fetched")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<StudentProfileDTO> getProfile(Authentication authentication) {
        String username = authentication.getName();
        Optional<Account> accountOpt = accountService.findByRegisterNumber(username);

        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Account acc = accountOpt.get();
        StudentProfileDTO profile = new StudentProfileDTO(
                acc.getRegisterNo(),
                acc.getAge(),
                acc.getBranch(),
                acc.getDepartment(),
                acc.getSection(),
                acc.getMobile_no(),
                acc.getEmail(),
                acc.getAcademicYear(),
                acc.getEvents_attended()
        );
        return ResponseEntity.ok(profile);
    }

    // ✅ Update Profile
    @PreAuthorize("hasAuthority('STUDENT')")
    @PutMapping("/profile/update")
    @Operation(summary = "Update student profile")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @ApiResponse(responseCode = "400", description = "Invalid update")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<StudentProfileDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody StudentProfileDTO dto) {
        String username = authentication.getName();
        Optional<Account> accountOpt = accountService.findByRegisterNumber(username);

        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Account acc = accountOpt.get();
        acc.setAge(dto.getAge());
        acc.setBranch(dto.getBranch());
        acc.setDepartment(dto.getDepartment());
        acc.setSection(dto.getSection());
        acc.setMobile_no(dto.getMobileNo());
        acc.setEmail(dto.getEmail());
        acc.setAcademicYear(dto.getAcademicYear());

        Account updated = accountService.save(acc);

        StudentProfileDTO response = new StudentProfileDTO(
                updated.getRegisterNo(),
                updated.getAge(),
                updated.getBranch(),
                updated.getDepartment(),
                updated.getSection(),
                updated.getMobile_no(),
                updated.getEmail(),
                updated.getAcademicYear(),
                updated.getEvents_attended()
        );

        return ResponseEntity.ok(response);
    }

    // ✅ Enroll in Event
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/enrollevent")
    @Operation(summary = "Enroll in an event")
    @ApiResponse(responseCode = "201", description = "Enrollment created")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<EnrollmentDTO> enroll(
            Authentication authentication,
            @Valid @RequestBody EnrollmentDTO dto) {
        String username = authentication.getName();
        Optional<Account> accOpt = accountService.findByRegisterNumber(username);

        if (accOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setAccountId(accOpt.get());
        enrollment.setEventId(dto.getEventId());
        enrollment.setStatus("ENROLLED");

        Enrollment saved = enrollmentService.save(enrollment);

        EnrollmentDTO response = new EnrollmentDTO(saved.getId(), saved.getAccountId(),saved.getEventId(), saved.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ View Student Enrollments
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

        List<EnrollmentDTO> enrollments = enrollmentService.findByAccountId(accOpt.get().getId())
                .stream()
                .map(e -> new EnrollmentDTO(e.getId(), e.getEventId(), e.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrollments);
    }

    // ✅ Submit Feedback
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

        FeedbackDTO response = new FeedbackDTO(saved.getId(), saved.getEventId(), saved.getComments(), saved.getRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Request Approval
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/approvalrequest")
    @Operation(summary = "Request approval")
    @ApiResponse(responseCode = "201", description = "Approval request submitted")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<ApprovalRequestDTO> requestApproval(
            Authentication authentication,
            @Valid @RequestBody ApprovalRequestDTO dto) {
        String username = authentication.getName();
        Optional<Account> accOpt = accountService.findByRegisterNumber(username);

        if (accOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ApprovalRequest req = new ApprovalRequest();
        req.setAccountId(accOpt.get());
        req.setRequestType(dto.getRequestType());
        req.setStatus("PENDING");

        ApprovalRequest saved = approvalService.save(req);

        ApprovalRequestDTO response = new ApprovalRequestDTO(saved.getId(), saved.getRequestType(), saved.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ View Approvals
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
                .map(a -> new ApprovalRequestDTO(a.getId(), a.getRequestType(), a.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(approvals);
    }

    // ✅ View Certificates
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/certificates")
    @Operation(summary = "Get student's certificates")
    @ApiResponse(responseCode = "200", description = "Certificates fetched")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<CertificateDTO>> myCertificates(Authentication authentication) {
        String username = authentication.getName();
        Optional<Account> accOpt = accountService.findByRegisterNumber(username);

        if (accOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<CertificateDTO> certs = certificateService.findByAccountId(accOpt.get().getId())
                .stream()
                .map(c -> new CertificateDTO(c.getId(), c.getTitle(), c.getIssuedDate()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(certs);
    }
}