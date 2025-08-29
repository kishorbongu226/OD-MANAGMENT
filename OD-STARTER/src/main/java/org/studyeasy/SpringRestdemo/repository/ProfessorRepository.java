package org.studyeasy.SpringRestdemo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.studyeasy.SpringRestdemo.model.ProfessorAccount;

public interface ProfessorRepository extends JpaRepository<ProfessorAccount, Long> {

    Optional<ProfessorAccount> findByRegisterNo(String registerNo);

    public boolean existsByRegisterNo(String registerNo);
    
}
