package org.studyeasy.SpringRestdemo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.repository.AccountRepository;
import org.studyeasy.SpringRestdemo.security.WebSecurityConfig;
import org.studyeasy.SpringRestdemo.util.constants.Authority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getAuthorities() == null) {
            account.setAuthorities(Authority.USER.toString());
        }
        return accountRepository.save(account);

    }

    public List<Account> findall() {

        return accountRepository.findAll();

    }

    public Optional<Account> findByRegisterNumber(String register_no) {
        return accountRepository.findByRegisterNo(register_no);

    }

    public Optional<Account> findByID(long id) {
        return accountRepository.findById(id);

    }

    public void deleteByID(long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String registerno) throws UsernameNotFoundException {
        log.info("Attempting to authenticate user with registerNo: {}", registerno);

        Optional<Account> optionalAccount = accountRepository.findByRegisterNo(registerno);
        log.debug("Result from accountRepository.findByRegisterNo({}): {}", registerno, optionalAccount);

        if (!optionalAccount.isPresent()) {
            log.warn("Authentication failed → Account not found for registerNo: {}", registerno);
            throw new UsernameNotFoundException("Account not found for registerNo: " + registerno);
        }

        Account account = optionalAccount.get();
        log.info("Account found: registerNo={}, role={}, enabled={}",
                account.getRegisterNo(),
                account.getAuthorities());

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(account.getAuthorities()));

        log.debug("Granted authorities for user {}: {}", registerno, grantedAuthorities);

        return new User(account.getRegisterNo(), account.getPassword(), grantedAuthorities);
    }

}
