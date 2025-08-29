package org.studyeasy.SpringRestdemo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.studyeasy.SpringRestdemo.model.ODRequest;
import org.studyeasy.SpringRestdemo.repository.ODRequestRepository;

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
}
