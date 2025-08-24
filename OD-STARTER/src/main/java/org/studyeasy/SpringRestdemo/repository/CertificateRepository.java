package org.studyeasy.SpringRestdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestdemo.model.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
 List<Certificate> findByAccount_RegisterNo(String registerNo);}
