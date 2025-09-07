package org.studyeasy.SpringRestdemo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

@Component
public class EventCompletionScheduler {

    private final EventService eventService;

    public EventCompletionScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    // Runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void autoMarkCompleted() {
        List<Event> approvedEvents = eventService.findByStatus(EventStatus.APPROVED);

        for (Event event : approvedEvents) {
            if (event.getEndTime().isBefore(LocalDateTime.now())) {
                event.setStatus(EventStatus.COMPLETED);
                eventService.save(event);
            }
        }
    }
}
