package org.studyeasy.SpringRestdemo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountService accountService;
    private final ProfessorService professorService;

    public CustomUserDetailsService(AccountService accountService, ProfessorService professorService) {
        this.accountService = accountService;
        this.professorService = professorService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // First check student accounts
            return accountService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            // If not found, check professors
            return professorService.loadUserByUsername(username);
        }
    }
}

