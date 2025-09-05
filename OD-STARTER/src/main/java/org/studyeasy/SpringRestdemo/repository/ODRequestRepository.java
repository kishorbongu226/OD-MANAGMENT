package org.studyeasy.SpringRestdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.Enrollment;
import org.studyeasy.SpringRestdemo.model.Event;
import org.studyeasy.SpringRestdemo.model.ODRequest;
import org.studyeasy.SpringRestdemo.util.constants.ODStatus;


@Repository
public interface ODRequestRepository extends JpaRepository<ODRequest,Long>{
    List<Event> findByStatus(ODStatus status);
     boolean existsByEnrollment(Enrollment enrollment);
         List<ODRequest> findByApproverAndStatus(String approver, ODStatus status);

}