package org.studyeasy.SpringRestdemo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.model.ODRequest;
import org.studyeasy.SpringRestdemo.payload.auth.EventRequestDTO;
import org.studyeasy.SpringRestdemo.payload.auth.EventResponseDTO;
import org.studyeasy.SpringRestdemo.payload.auth.ODResponseDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.ApprovalRequestDTO;
import org.studyeasy.SpringRestdemo.service.ApprovalService;
import org.studyeasy.SpringRestdemo.service.EnrollmentService;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.service.ODRequestService;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;
import org.studyeasy.SpringRestdemo.util.constants.ODStatus;
import org.studyeasy.SpringRestdemo.util.constants.RequestStatus;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/teacher")
@Tag(name = "Teacher Controller", description = "Controller for teacher related operations")
@Slf4j
public class TeacherController {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private EventService eventService;
    @Autowired
    private ODRequestService odRequestService;

    @Autowired
    private EnrollmentService enrollmentService;
    

    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @GetMapping("/od-requests")
    public ResponseEntity<List<ApprovalRequestDTO>> getODRequests(Authentication authentication) {
        String teacherRegNo = authentication.getName();
        List<ApprovalRequestDTO> requests = approvalService.findByApproverAndStatus(teacherRegNo, RequestStatus.PENDING)
            .stream()
            .map(r -> new ApprovalRequestDTO(r.getId(), r.getRegisterNo(), r.getStatus()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    //  Approve OD
    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @PostMapping("/od-requests/{id}/approve")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<ODResponseDTO> approveOD(@PathVariable Long id) {
        ODRequest odRequest = odRequestService.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        odRequest.setStatus(ODStatus.APPROVED);
        ODRequest saved = odRequestService.save(odRequest);
        return ResponseEntity.ok(new ODResponseDTO(saved.getId(), saved.getEnrollment().getAccount().getRegisterNo(), saved.getStatus()));
    }

    // Decline OD
    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @PostMapping("/od-requests/{id}/decline")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<ODResponseDTO> declineOD(@PathVariable Long id) {
        ODRequest req =  odRequestService.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus(ODStatus.DECLINED);
        ODRequest saved =  odRequestService.save(req);
        return ResponseEntity.ok(new ODResponseDTO(saved.getId(), saved.getEnrollment().getAccount().getRegisterNo(), saved.getStatus()));
    }

    // Submit Event (goes into PENDING state)
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/events/submit")
    @SecurityRequirement(name="studyeasy-demo-api")

    public ResponseEntity<EventResponseDTO> submitEvent(@Valid @RequestBody EventRequestDTO dto,Authentication authentication) {
        String register = authentication.getName();
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setStatus(EventStatus.PENDING);
        event.setEventCordinator(dto.getEventCordinator());
        event.setEligibleYears(dto.getEligibleYears());
        event.setCreatedBy(register);

        Event saved = eventService.save(event);

        EventResponseDTO response = new EventResponseDTO(
            saved.getId(),
            saved.getTitle(),
            saved.getDescription(),
            saved.getLocation(),
            saved.getStartTime(),
            saved.getEndTime(),
            saved.getEventCordinator(),
            saved.getEligibleYears(),
            saved.getStatus()
        );
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @GetMapping("/my-events/event-cordinator")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<EventResponseDTO>> getMyEvents(Authentication authentication) {
        String teacherRegNo = authentication.getName();

        List<Event> events = eventService.findByEventCordinator(teacherRegNo);

        List<EventResponseDTO> response = events.stream()
            .map(e -> new EventResponseDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getLocation(),
                e.getStartTime(),
                e.getEndTime(),
                e.getEventCordinator(),
                e.getEligibleYears(),
                e.getStatus()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @GetMapping("/events/{eventId}/students")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<String>> getStudentsForEvent(@PathVariable Long eventId) {
        List<Enrollment> enrollments = enrollmentService.findByEventId(eventId);

        // Extract student register numbers (or names, emails, etc.)
        List<String> students = enrollments.stream()
            .map(e -> e.getAccount().getRegisterNo()) // adjust based on your Account model
            .collect(Collectors.toList());

        return ResponseEntity.ok(students);
    }


}

