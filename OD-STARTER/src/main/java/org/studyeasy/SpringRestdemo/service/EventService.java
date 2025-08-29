package org.studyeasy.SpringRestdemo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.repository.EventRepository;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

@Transactional
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

   public List<Event> getUpcomingApprovedEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByStatus(EventStatus.APPROVED)
                .stream()
                .filter(event -> event.getStartTime().isAfter(now)) // upcoming only
                .toList();
    }
   public List<Event> getOngoingApprovedEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByStatus(EventStatus.APPROVED)
                .stream()
                .filter(event -> event.getStartTime().isBefore(now)&&event.getEndTime().isAfter(now) )// upcoming only
                .toList();
    }

    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }
   

    public Event createEvent(Event event, String role) {
    if ("ADMIN".equals(role)) {
        event.setStatus(EventStatus.APPROVED);  // Directly approved
    } else {
        event.setStatus(EventStatus.PENDING);   // Needs admin approval
    }
    return eventRepository.save(event);
    }

    public List<Event> findByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

   


}

