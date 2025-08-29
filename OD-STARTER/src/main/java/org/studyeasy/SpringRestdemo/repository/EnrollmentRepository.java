package org.studyeasy.SpringRestdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByAccount_RegisterNo(String registerNo);

    // 🔑 New method: Find specific enrollment for event + student
    java.util.Optional<Enrollment> findByEvent_IdAndAccount_RegisterNo(Long eventId, String registerNo);

    // (Optional) Get all enrollments of an event
    List<Enrollment> findByEvent_Id(Long eventId);
}

