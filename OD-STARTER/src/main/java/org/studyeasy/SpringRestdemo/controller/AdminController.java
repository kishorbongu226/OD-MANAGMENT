package org.studyeasy.SpringRestdemo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.payload.auth.EventRequestDTO;
import org.studyeasy.SpringRestdemo.payload.auth.EventResponseDTO;
import org.studyeasy.SpringRestdemo.service.AdminService;
import org.studyeasy.SpringRestdemo.util.constants.EventError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Tag(name="Event Controller",description = "controller for event management")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService eventService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value="/events/add",consumes="application/json",produces="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode="400",description="please ass valid name and description")
    @ApiResponse(responseCode="201",description="Event added")
    @Operation(summary="Add an event")
    @SecurityRequirement(name="studyeasy-demo-api")
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO request,Authentication authentication) {
        try{
                Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());

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
        catch(Exception e)
        {
                log.debug(EventError.ADD_EVENT_ERROR.toString()+": "+e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // ✅ Get Event by ID
//     @GetMapping("/{id}")
//     public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id) {
//         Event event = eventService.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Event not found"));

//         EventResponseDTO response = new EventResponseDTO(
//                 event.getId(),
//                 event.getTitle(),
//                 event.getDescription(),
//                 event.getLocation(),
//                 event.getStartTime(),
//                 event.getEndTime()
//         );

//         return ResponseEntity.ok(response);
//     }

    // ✅ Get All Events
    @GetMapping(value="/events",produces="application/json")
    @ApiResponse(responseCode="200",description="List of events")
    @ApiResponse(responseCode="401",description="Token Missing")
    @ApiResponse(responseCode="403",description="Token Error")
    @Operation(summary = "List event api")
    @SecurityRequirement(name="studyeasy-demo-api")
    public List<EventResponseDTO> events(Authentication authentication) {
        List<EventResponseDTO> events = eventService.findAll()
                .stream()
                .map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getLocation(),
                        event.getStartTime(),
                        event.getEndTime()
                ))
                .collect(Collectors.toList());

        return events;
    }

    // ✅ Update Event
    @PutMapping(value="/events/{id}/update",consumes="application/json",produces="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode="400",description="please add valid name and description")
    @ApiResponse(responseCode="204",description="Event update")
    @Operation(summary="Update an event")
    @SecurityRequirement(name="studyeasy-demo-api")
    public ResponseEntity<EventResponseDTO> updateEvent(@Valid
            @PathVariable Long id,
            @RequestBody EventRequestDTO request,CouchbaseProperties.Authentication authentication) {

        try
        {
                Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());

        Event updated = eventService.save(event);

        EventResponseDTO response = new EventResponseDTO(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getLocation(),
                updated.getStartTime(),
                updated.getEndTime()
        );

        return ResponseEntity.ok(response);
        }
        catch(Exception e)
        {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // ✅ Delete Event
    @DeleteMapping("/events/{id}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode="202",description="Event deleted")
    @Operation(summary="delete an event")
    @SecurityRequirement(name="studyeasy-demo-api")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id,Authentication authentication) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventService.deleteById(event.getId());
        return ResponseEntity.ok("Event deleted successfully");
    }
}
