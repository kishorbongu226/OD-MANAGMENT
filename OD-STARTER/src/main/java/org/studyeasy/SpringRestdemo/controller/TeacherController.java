package org.studyeasy.SpringRestdemo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.model.ODRequest;
import org.studyeasy.SpringRestdemo.payload.auth.EventRequestDTO;
import org.studyeasy.SpringRestdemo.payload.auth.EventResponseDTO;
import org.studyeasy.SpringRestdemo.service.ApprovalService;
import org.studyeasy.SpringRestdemo.service.EnrollmentService;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.service.ODRequestService;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;
import org.studyeasy.SpringRestdemo.util.constants.ODStatus;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
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

    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);
    

    @GetMapping("/events/status")
    public String getEventStatus(Model model){
        List<Event> events = eventService.findAll();
        model.addAttribute("events", events);
        return "teacherApproval";
    }

    // @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @GetMapping("/events/od-requests")
    public String getODRequests(Model model) {
        String teacherRegNo = "P1001";
        List<ODRequest> requests = odRequestService.findByApproverAndStatus(teacherRegNo, ODStatus.PENDING);
        model.addAttribute("odRequests", requests);
        // model.addAttribute("odRequests", new ODRequest());
        return "teacherOdapproval";
    }

    //  Approve OD
    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @PostMapping("/od-requests/{id}/approve")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public String approveOD(@PathVariable Long id) {
        ODRequest odRequest = odRequestService.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        odRequest.setStatus(ODStatus.APPROVED);
        odRequestService.save(odRequest);
        logger.info("Request with id {} saved successfully", id);
        return "redirect:/api/v1/teacher/events/od-requests";
    }

    // Decline OD
    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @PostMapping("/od-requests/{id}/decline")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public String declineOD(@PathVariable Long id) {
        ODRequest req =  odRequestService.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus(ODStatus.DECLINED);
        odRequestService.save(req);
        logger.info("Request with id {} saved successfully", id);
        return "redirect:/api/v1/teacher/events/od-requests";
    }




    
    @GetMapping("/events/Upcoming")
    public String getUpcomingevents(Model model) {

        List<Event> events = eventService.getUpcomingApprovedEvents();
        model.addAttribute("events", events);
        model.addAttribute("event", new Event());

        return "teacherUpcoming";
    }
    @GetMapping("/events/Ongoing")
    public String getOngoingevents(Model model) {

        List<Event> events = eventService.getOngoingApprovedEvents();
        model.addAttribute("events", events);
        model.addAttribute("event", new Event());

        return "teacherOngoing";
    }


    // Submit Event (goes into PENDING state)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/events/add")

    @SecurityRequirement(name = "studyeasy-demo-api")
    public String createEvent(@Valid @ModelAttribute Event event, BindingResult bindingResult) {

        log.info("➡️ Received request to create event");

        if (bindingResult.hasErrors()) {
            log.warn("❌ Validation failed for event: {}", event);
            return "event-form"; // back to form page
        }

        log.debug(
                "Event data received: title={}, type={}, venue={}, start={}, end={}, coordinator={}, eligibleYears={}",
                event.getTitle(), event.getType(), event.getLocation(),
                event.getStartTime(), event.getEndTime(),
                event.getEventCordinator(), event.getEligibleYears());

        Event create_event = new Event();
        create_event.setType(event.getType());
        create_event.setTitle(event.getTitle());
        create_event.setDescription(event.getDescription());
        create_event.setEventCordinator(event.getEventCordinator());
        create_event.setLocation(event.getLocation());
        create_event.setStartTime(event.getStartTime());
        create_event.setEndTime(event.getEndTime());
        create_event.setEligibleYears(event.getEligibleYears());
        create_event.setStatus(EventStatus.PENDING);

        log.info("📌 Saving new event: {}", create_event.getTitle());
        eventService.save(create_event);
        log.info("✅ Event '{}' created successfully", create_event.getTitle());

        return "redirect:/api/v1/teacher/events/Upcoming";
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

