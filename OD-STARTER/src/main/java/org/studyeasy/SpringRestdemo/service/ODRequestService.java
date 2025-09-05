package org.studyeasy.SpringRestdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.ODRequest;
import org.studyeasy.SpringRestdemo.repository.ODRequestRepository;
import org.studyeasy.SpringRestdemo.util.constants.ODStatus;

@Service
@Transactional
public class ODRequestService {
    @Autowired
    private ODRequestRepository odRequestRepository;

    public ODRequest save(ODRequest odRequest) {
        return odRequestRepository.save(odRequest);
    }

    public Optional<ODRequest> findById(Long id) {
        return odRequestRepository.findById(id);
    }

    public boolean existsByEnrollment(Enrollment enrollment) {
        return odRequestRepository.existsByEnrollment(enrollment);
    }

    public List<ODRequest> findByApproverAndStatus(String approver, ODStatus status) {
    return odRequestRepository.findByApproverAndStatus(approver, status);
    }
}
