package org.studyeasy.SpringRestdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.ApprovalRequest;
import org.studyeasy.SpringRestdemo.util.constants.RequestStatus;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    List<ApprovalRequest> findByRegisterNo(String registernNo);
    List<ApprovalRequest> findByStatus(String status);
    List<ApprovalRequest> findByApproverAndStatus(String approver, RequestStatus status);

}
