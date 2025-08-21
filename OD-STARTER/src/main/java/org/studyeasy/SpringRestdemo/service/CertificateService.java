package org.studyeasy.SpringRestdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestdemo.model.Certificate;
import org.studyeasy.SpringRestdemo.repository.CertificateRepository;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate save(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Optional<Certificate> findById(Long id) {
        return certificateRepository.findById(id);
    }

    public List<Certificate> findAll() {
        return certificateRepository.findAll();
    }

    public List<Certificate> findByAccountId(String registerNo) {
        return certificateRepository.findByResisterNo(registerNo);
    }

    public void deleteById(Long id) {
        certificateRepository.deleteById(id);
    }
}
