package org.studyeasy.SpringRestdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestdemo.model.ApprovalRequest;
import org.studyeasy.SpringRestdemo.repository.ApprovalRequestRepository;
import org.studyeasy.SpringRestdemo.util.constants.RequestStatus;

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    public ApprovalRequest save(ApprovalRequest approvalRequest) {
        return approvalRequestRepository.save(approvalRequest);
    }

    public Optional<ApprovalRequest> findById(Long id) {
        return approvalRequestRepository.findById(id);
    }

    public List<ApprovalRequest> findAll() {
        return approvalRequestRepository.findAll();
    }

    public List<ApprovalRequest> findByAccountId(String registernNo) {
        return approvalRequestRepository.findByRegisterNo(registernNo);
    }

    public List<ApprovalRequest> findByStatus(String status) {
        return approvalRequestRepository.findByStatus(status);
    }

    public List<ApprovalRequest> findByApproverAndStatus(String approver, RequestStatus status) {
    return approvalRequestRepository.findByApproverAndStatus(approver, status);
}


    public void deleteById(Long id) {
        approvalRequestRepository.deleteById(id);
    }
}
