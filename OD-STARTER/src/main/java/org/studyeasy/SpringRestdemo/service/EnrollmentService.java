package org.studyeasy.SpringRestdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.repository.AccountRepository;
import org.studyeasy.SpringRestdemo.repository.EnrollmentRepository;
import org.studyeasy.SpringRestdemo.util.constants.AttendenceStatus;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> findByRegisterNo(String register)
    {
        return enrollmentRepository.findByAccount_RegisterNo(register);
    }

    
    public void deleteById(Long id) {
        enrollmentRepository.deleteById(id);
    }

    public Optional<Enrollment> findByAccountAndEvent(Account account, Event event) {
    return enrollmentRepository.findByAccountAndEvent(account, event);
}

public List<Enrollment> findByEventAndAttendenceStatus(Event event, AttendenceStatus status) {
    return enrollmentRepository.findByEventAndAttendenceStatus(event, status);
}



public void saveAll(List<Enrollment> enrollments) {
    enrollmentRepository.saveAll(enrollments);
}


public List<Enrollment> findByEventId(Long eventId) {
        return enrollmentRepository.findByEventId(eventId);
    }

    public long countStudentsPresent() {
    return enrollmentRepository.countByAttendenceStatus(AttendenceStatus.PRESENT);
}

public long countEnrolledStudents() {
    return enrollmentRepository.count();
}

public List<Enrollment> findByAccountAndAttendenceStatus(Account account, AttendenceStatus status) {
    return enrollmentRepository.findByAccountAndAttendenceStatus(account, status);
}
}



   

