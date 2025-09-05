package org.studyeasy.SpringRestdemo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.util.constants.AttendenceStatus;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {


    Optional<Enrollment> findByAccountAndEvent(Account account, Event event);
    List<Enrollment> findByEventAndAttendenceStatus(Event event, AttendenceStatus status);

              List<Enrollment> findByAccount_RegisterNo(String registerNo);

              List<Enrollment> findByEventId(Long eventId);


}

