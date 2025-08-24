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
import org.studyeasy.SpringRestdemo.model.ApprovalRequest;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.payload.auth.EventRequestDTO;
import org.studyeasy.SpringRestdemo.payload.auth.EventResponseDTO;
import org.studyeasy.SpringRestdemo.payload.auth.student.ApprovalRequestDTO;
import org.studyeasy.SpringRestdemo.service.ApprovalService;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;
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

    // ✅ Approve OD
    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @PostMapping("/od-requests/{id}/approve")
    public ResponseEntity<ApprovalRequestDTO> approveOD(@PathVariable Long id) {
        ApprovalRequest req = approvalService.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus(RequestStatus.APPROVED);
        ApprovalRequest saved = approvalService.save(req);
        return ResponseEntity.ok(new ApprovalRequestDTO(saved.getId(), saved.getRegisterNo(), saved.getStatus()));
    }

    // ✅ Decline OD
    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @PostMapping("/od-requests/{id}/decline")
    public ResponseEntity<ApprovalRequestDTO> declineOD(@PathVariable Long id) {
        ApprovalRequest req = approvalService.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus(RequestStatus.DECLINED);
        ApprovalRequest saved = approvalService.save(req);
        return ResponseEntity.ok(new ApprovalRequestDTO(saved.getId(), saved.getRegisterNo(), saved.getStatus()));
    }

    // ✅ Submit Event (goes into PENDING state)
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/events/submit")
        @SecurityRequirement(name="studyeasy-demo-api")

    public ResponseEntity<EventResponseDTO> submitEvent(@Valid @RequestBody EventRequestDTO dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setStatus(EventStatus.PENDING);

        Event saved = eventService.save(event);

        EventResponseDTO response = new EventResponseDTO(
            saved.getId(),
            saved.getTitle(),
            saved.getDescription(),
            saved.getLocation(),
            saved.getStartTime(),
            saved.getEndTime()
        );
        return ResponseEntity.ok(response);
    }
}

