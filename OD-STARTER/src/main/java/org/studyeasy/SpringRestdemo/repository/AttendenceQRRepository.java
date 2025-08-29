package org.studyeasy.SpringRestdemo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.AttendenceQR;

@Repository
public interface AttendenceQRRepository extends JpaRepository<AttendenceQR, Long> {
    Optional<AttendenceQR> findByTokenAndActiveTrue(String token);
    List<AttendenceQR> findByEventId(Long eventId);
}

