package org.studyeasy.SpringRestdemo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class ProfessorService implements UserDetailsService{
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ProfessorAccount save(ProfessorAccount account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getAuthorities() == null){
            account.setAuthorities(Authority.USER.toString());
        }
        return professorRepository.save(account);
        
    }

    public List<ProfessorAccount> findall(){

        return professorRepository.findAll();

    }

    public Optional<ProfessorAccount> findByRegisterNumber(String registerNo){
        return professorRepository.findByRegisterNo(registerNo);
        
    }

    public Optional<ProfessorAccount> findByID(long id) {
        return professorRepository.findById(id);
        
    }

    public void deleteByID(long id) {
        professorRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String registerno) throws UsernameNotFoundException {
       Optional<ProfessorAccount> optionaAccount =  professorRepository.findByRegisterNo(registerno);
       if (!optionaAccount.isPresent()){
            throw new UsernameNotFoundException("Account not found");
       }
       ProfessorAccount account = optionaAccount.get();

       List<GrantedAuthority> grantedAuthoriy = new ArrayList<>();
       grantedAuthoriy.add(new SimpleGrantedAuthority(account.getAuthorities()));
       return new User(account.getRegisterNo(),account.getPassword(),grantedAuthoriy);
    }

}
