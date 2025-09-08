package org.studyeasy.SpringRestdemo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.payload.auth.EventResponseDTO;
import org.studyeasy.SpringRestdemo.service.EnrollmentService;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api/v1")
@Tag(name = "Event Controller", description = "controller for event management")
@Slf4j
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EnrollmentService enrollmentService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

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

        log.info("📌 Saving new event: {}", create_event.getTitle());
        eventService.createEvent(create_event, "ADMIN");
        log.info("✅ Event '{}' created successfully", create_event.getTitle());

        return "redirect:/api/v1/teacher/events/Upcoming";
    }

    @GetMapping("/events/add")
    public String getUpcomingevents(Model model) {

        List<Event> events = eventService.findAll();
        model.addAttribute("events", events);
        model.addAttribute("event", new Event());

        return "adminUpcoming";
    }

    // Get Event by ID
    // @GetMapping("/{id}")
    // public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id) {
    // Event event = eventService.findById(id)
    // .orElseThrow(() -> new RuntimeException("Event not found"));

    // EventResponseDTO response = new EventResponseDTO(
    // event.getId(),
    // event.getTitle(),
    // event.getDescription(),
    // event.getLocation(),
    // event.getStartTime(),
    // event.getEndTime()
    // );

    // return ResponseEntity.ok(response);
    // }

    @GetMapping("/events/pending")
    public String getPendingEvents(Model model) {
        List<Event> events = eventService.findByStatus(EventStatus.PENDING);

        List<EventResponseDTO> response = events.stream()
                .map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getLocation(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getEventCordinator(),
                        event.getEligibleYears(),
                        event.getStatus()))
                .toList();

        // Add data to the model
        model.addAttribute("events", response);

        // return the name of your HTML template (e.g., pending-events.html in
        // templates/)
        return "redirect:/api/v1/events";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/events/{id}/approve")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public String approveEvent(@PathVariable Long id) {
        logger.info("Received request to approve event with id: {}", id);

        Event event = eventService.findById(id)
                .orElseThrow(() -> {
                    logger.error("Event with id {} not found", id);
                    return new RuntimeException("Event not found");
                });
        logger.debug("Fetched event from DB: {}", event);

        event.setStatus(EventStatus.APPROVED);
        logger.info("Updated event status to APPROVED for id: {}", id);

        eventService.save(event);
        logger.info("Event with id {} saved successfully", id);

        logger.info("Redirecting to /events after approval");
        return "redirect:/api/v1/events";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/events/{id}/decline")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public String declineEvent(@PathVariable Long id) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(EventStatus.DECLINED);
        eventService.save(event);

        // Redirect to the event list page
        return "redirect:/api/v1/events";
    }

    // ✅ Get All Events

    @GetMapping("/events")
    public String eventsPage(Model model) {
        List<EventResponseDTO> events = eventService.findAll()
                .stream()
                .map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getLocation(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getEventCordinator(),
                        event.getEligibleYears(),
                        event.getStatus()))
                .toList();

        model.addAttribute("events", events);
        return "adminApproval"; // maps to templates/events.html
    }

    // ✅ Update Event
    // @PutMapping(value="/events/{id}/update",consumes="application/json",produces="application/json")
    // @ResponseStatus(HttpStatus.CREATED)
    // @ApiResponse(responseCode="400",description="please add valid name and
    // description")
    // @ApiResponse(responseCode="204",description="Event update")
    // @Operation(summary="Update an event")
    // @SecurityRequirement(name="studyeasy-demo-api")
    // public ResponseEntity<EventResponseDTO> updateEvent(@Valid
    // @PathVariable Long id,
    // @RequestBody EventRequestDTO request,Authentication authentication) {

    // try
    // {
    // Event event = eventService.findById(id)
    // .orElseThrow(() -> new RuntimeException("Event not found"));

    // event.setTitle(request.getTitle());
    // event.setDescription(request.getDescription());
    // event.setLocation(request.getLocation());
    // event.setStartTime(request.getStartTime());
    // event.setEndTime(request.getEndTime());

    // Event updated = eventService.save(event);

    // EventResponseDTO response = new EventResponseDTO(
    // updated.getId(),
    // updated.getTitle(),
    // updated.getDescription(),
    // updated.getLocation(),
    // updated.getStartTime(),
    // updated.getEndTime(),
    // updated.getEventCordinator(),
    // updated.getEligibleYears(),
    // updated.getStatus()
    // );

    // return ResponseEntity.ok(response);
    // }
    // catch(Exception e)
    // {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    // }
    // }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Request received to edit event with id: {}", id);

        Event event = eventService.findById(id)
                .orElseThrow(() -> {
                    logger.error("Event not found with id: {}", id);
                    return new RuntimeException("Event not found");
                });

        logger.debug("Loaded event for edit: {}", event);
        model.addAttribute("event", event);
        return "adminUpcoming"; 
    }

    @PostMapping("/{id}/update")
    public String updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("event") Event event,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        logger.info("Request received to update event with id: {}", id);

        if (result.hasErrors()) {
            logger.warn("Validation errors while updating event with id {}: {}", id, result.getAllErrors());
            return "adminUpcoming"; // redisplay with validation errors
        }

        Event existing = eventService.findById(id)
                .orElseThrow(() -> {
                    logger.error("Event not found while updating, id: {}", id);
                    return new RuntimeException("Event not found");
                });

        logger.debug("Existing event before update: {}", existing);

        existing.setTitle(event.getTitle());
        existing.setDescription(event.getDescription());
        existing.setLocation(event.getLocation());
        existing.setStartTime(event.getStartTime());
        existing.setEndTime(event.getEndTime());
        existing.setType(event.getType());
        existing.setEligibleYears(event.getEligibleYears());

        eventService.save(existing);

        logger.info("Event with id {} updated successfully", id);

        redirectAttributes.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/events/add"; // back to list after update
    }

    // ✅ Delete Event
    @DeleteMapping("/events/{id}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "202", description = "Event deleted")
    @Operation(summary = "delete an event")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id, Authentication authentication) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventService.deleteById(event.getId());
        return ResponseEntity.ok("Event deleted successfully");
    }

    @GetMapping("/events/ongoing")
    public String ongoingEventsPage(Model model) {
        List<Event> events = eventService.getOngoingApprovedEvents();

        model.addAttribute("events", events);
        return "adminOngoing"; // maps to templates/adminOngoing.html
    }

    // @GetMapping("/events/ongoing/api")
    // @ResponseBody
    // public List<Event> getOngoingEvents() {
    // logger.info("API call: Fetching ongoing approved events...");
    // List<Event> events = eventService.getOngoingApprovedEvents();
    // logger.info("API call: Number of ongoing approved events returned: {}",
    // events.size());
    // return events;
    // }

    @PreAuthorize("hasAuthority('ADMIN')")
@GetMapping("/admin/events/statistics")
public String getEventStatistics(Model model) {
    long completedEvents = eventService.findByStatus(EventStatus.APPROVED).size();
    long studentsPresent = enrollmentService.countStudentsPresent();
    long certificatesIssued = enrollmentService.countStudentsPresent(); // custom method

    model.addAttribute("completedEvents", completedEvents);
    model.addAttribute("studentsPresent", studentsPresent);
    model.addAttribute("certificatesIssued", certificatesIssued);

    return "adminCompleted"; // Thymeleaf template name
}

@PreAuthorize("hasAuthority('ADMIN')")
@GetMapping("/admin/events/completed")
public String getCompletedEvents(Model model) {
    List<Event> completedEvents = eventService.findByStatus(EventStatus.COMPLETED);
    model.addAttribute("events", completedEvents);

    return "adminCompleted"; 
}


@PreAuthorize("hasAuthority('ADMIN')")
@GetMapping("/admin/events/enrolled-students")
public String countEnrolledStudents(Model model) {
    long enrolledStudents = enrollmentService.countEnrolledStudents();
    model.addAttribute("enrolled", enrolledStudents);
    return "adminCompleted";
}


}
