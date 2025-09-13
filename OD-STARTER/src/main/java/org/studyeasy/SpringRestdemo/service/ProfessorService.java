package org.studyeasy.SpringRestdemo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.studyeasy.SpringRestdemo.model.ProfessorAccount;
import org.studyeasy.SpringRestdemo.repository.ProfessorRepository;
import org.studyeasy.SpringRestdemo.util.constants.Authority;

@Service
public class ProfessorService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(ProfessorService.class);

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ProfessorAccount save(ProfessorAccount account) {
        logger.info("Saving ProfessorAccount with register number: {}", account.getRegisterNo());

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (account.getAuthorities() == null) {
            logger.info("No authority set. Defaulting to USER role.");
            account.setAuthorities(Authority.USER.toString());
        }

        ProfessorAccount savedAccount = professorRepository.save(account);
        logger.debug("ProfessorAccount saved successfully: {}", savedAccount);
        return savedAccount;
    }

    public List<ProfessorAccount> findall() {
        logger.info("Fetching all ProfessorAccounts");
        List<ProfessorAccount> accounts = professorRepository.findAll();
        logger.debug("Total accounts fetched: {}", accounts.size());
        return accounts;
    }

    public Optional<ProfessorAccount> findByRegisterNumber(String registerNo) {
        logger.info("Looking up ProfessorAccount by register number: {}", registerNo);
        return professorRepository.findByRegisterNo(registerNo);
    }

    public Optional<ProfessorAccount> findByID(long id) {
        logger.info("Looking up ProfessorAccount by ID: {}", id);
        return professorRepository.findById(id);
    }

    public void deleteByID(long id) {
        logger.info("Deleting ProfessorAccount with ID: {}", id);
        professorRepository.deleteById(id);
        logger.debug("ProfessorAccount with ID: {} deleted", id);
    }

    @Override
    public UserDetails loadUserByUsername(String registerno) throws UsernameNotFoundException {
        logger.info("Loading user by register number: {}", registerno);

        Optional<ProfessorAccount> optionalAccount = professorRepository.findByRegisterNo(registerno);
        if (!optionalAccount.isPresent()) {
            logger.warn("No account found with register number: {}", registerno);
            throw new UsernameNotFoundException("Account not found");
        }

        ProfessorAccount account = optionalAccount.get();
        logger.debug("Account found: {}", account);

        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        grantedAuthority.add(new SimpleGrantedAuthority(account.getAuthorities()));

        logger.info("Returning UserDetails for register number: {}", registerno);
        return new User(account.getRegisterNo(), account.getPassword(), grantedAuthority);
    }
}
