// package org.studyeasy.SpringRestdemo.service;

// import java.time.LocalDateTime;
// import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.studyeasy.SpringRestdemo.model.Event;
// import org.studyeasy.SpringRestdemo.util.constants.EventStatus;

// @Component
// public class EventCompletionScheduler {


//      private static final Logger log = LoggerFactory.getLogger(EventCompletionScheduler.class);
//     private final EventService eventService;

//     public EventCompletionScheduler(EventService eventService) {
//         this.eventService = eventService;
//     }

//     // Runs every 5 minutes
//    // Runs every 5 minutes
//     @Scheduled(fixedRate = 300000)
//     public void autoMarkCompleted() {
//         log.info("⏰ Running autoMarkCompleted scheduler at {}", LocalDateTime.now());

//         List<Event> approvedEvents = eventService.findByStatus(EventStatus.APPROVED);
//         log.info("🔎 Found {} approved events to check for completion", approvedEvents.size());

//         for (Event event : approvedEvents) {
//             log.debug("➡️ Checking event ID={}, title={}, endTime={}", 
//                       event.getId(), event.getTitle(), event.getEndTime());

//             if (event.getEndTime() == null) {
//                 log.warn("⚠️ Event ID={} has no endTime set, skipping...", event.getId());
//                 continue;
//             }

//             if (event.getEndTime().isBefore(LocalDateTime.now())) {
//                 log.info("📌 Marking event ID={} ('{}') as COMPLETED", event.getId(), event.getTitle());
//                 event.setStatus(EventStatus.COMPLETED);
//                 eventService.save(event);
//                 log.info("✅ Event ID={} marked as COMPLETED and saved", event.getId());
//             } else {
//                 log.debug("⏭️ Event ID={} ('{}') not yet ended, keeping status={}", 
//                           event.getId(), event.getTitle(), event.getStatus());
//             }
//         }

//         log.info("✔️ autoMarkCompleted run finished at {}", LocalDateTime.now());
//     }
// }


