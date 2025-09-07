package org.studyeasy.SpringRestdemo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.service.EventService;
import org.studyeasy.SpringRestdemo.util.constants.QRCodeGenerator;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/api/v1/teacher")
@Tag(name = "Teacher Controller", description = "Controller for teacher related operations")
@Slf4j
public class AttendanceController {
    
    @Autowired
    private EventService eventService;
    


@GetMapping("/events/")
   public String getAllEvents(Model model) {

    
    List<Event> events = eventService.findAll(); // fetch all events
    model.addAttribute("events", events); // add list to model
    return "teacherAttendance"; // points to eventList.html in templates/
}

@GetMapping("/event/{id}")
public String getEventByID(@PathVariable Long id,Model model){

    List<Event> events = eventService.findAll();
    Optional<Event> optionalEvent = eventService.findById(id);
    Event event = optionalEvent.get();
    model.addAttribute("event", event);
    model.addAttribute("events", events);



    return "teacherAttendance";
}




  @PostMapping("/event/{id}/generateQR")
    public String generateQR(@PathVariable long id, Model model) {
        try {
            // Fetch event by id
            Optional<Event> optionalEvent = eventService.findById(id);
            if (!optionalEvent.isPresent()) {
                model.addAttribute("error", "Event not found");
                return "error";
            }

            Event event = optionalEvent.get();
            List<Event> events = eventService.findAll();

            // QR code text (can be event key, link, etc.)
            String qrText = event.getEventKey();

            // Generate QR in base64
            String qrCodeBase64 = QRCodeGenerator.generateQRCodeImage(qrText, 300, 300);

            // Add to model
            model.addAttribute("qrCode", qrCodeBase64);
            model.addAttribute("event", event);
            model.addAttribute("events", events);

            return "teacherAttendance"; // Thymeleaf template
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error generating QR code");
            return "error";
        }
    }


}
