package org.studyeasy.SpringRestdemo.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.util.constants.EventStatus;


@Repository
public interface EventRepository extends JpaRepository<Event,Long>{
    List<Event> findByStatus(EventStatus status);
}
