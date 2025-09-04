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
    
    @PreAuthorize("hasAuthority('SCOPE_TEACHER')")
    @GetMapping("/event/{id}")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public  String getEvent(@PathVariable Long id,Model model){
        Optional<Event> optionalEvent = eventService.findById(id);
        if(optionalEvent.isPresent()){
            Event event = optionalEvent.get();
            model.addAttribute("event",event);
            return "qrCode";
        }

        
return "Not Found ";
    }

@GetMapping("/events/")
   public String getAllEvents(Model model) {
    List<Event> events = eventService.findAll(); // fetch all events
    model.addAttribute("events", events); // add list to model
    return "eventList"; // points to eventList.html in templates/
}

@PostMapping("/event/{id}")
public String generateQR(@PathVariable long id, Model model) {
    try {
        // Example: encode event link or event details
        Optional<Event> optionalEvent= eventService.findById(id) ;
        Event event = optionalEvent.get();
        String qrText =   event.getEventKey();

        String qrCodeBase64 = QRCodeGenerator.generateQRCodeImage(qrText, 300, 300);

        // Add QR Code to model
        model.addAttribute("qrCode", qrCodeBase64);
         model.addAttribute("event", event);
            


        return "qrCode"; // qrView.html inside templates/
    } catch (Exception e) {
        e.printStackTrace();
        return "error";
    }
}


}
