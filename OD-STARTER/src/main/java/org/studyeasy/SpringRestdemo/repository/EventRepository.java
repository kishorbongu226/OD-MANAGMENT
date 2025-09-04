package org.studyeasy.SpringRestdemo.repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;


@Repository
public interface EventRepository extends JpaRepository<Event,Long>{
    
    List<Event> findByEventCordinator(String eventCordinator);

    Optional<Event> findByEventKey(String eventKey);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByEndTime(LocalDateTime time);
}
