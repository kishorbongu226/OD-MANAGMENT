package org.studyeasy.SpringRestdemo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.ProfessorAccount;
import org.studyeasy.SpringRestdemo.payload.auth.ProfessorProfileDTO;
import org.studyeasy.SpringRestdemo.payload.auth.ProfileDTO;
import org.studyeasy.SpringRestdemo.payload.auth.TokenDTO;
import org.studyeasy.SpringRestdemo.payload.auth.UserLoginDTO;
import org.studyeasy.SpringRestdemo.service.AccountService;
import org.studyeasy.SpringRestdemo.service.ProfessorService;
import org.studyeasy.SpringRestdemo.service.TokenService;
import org.studyeasy.SpringRestdemo.util.constants.AccountError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "Controller for Account management")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProfessorService professorService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(userLogin.getRegister_no(), userLogin.getPassword()));
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
        } catch (Exception e) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value = "/profile", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "View profile")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token Error")
    @Operation(summary = "View profile")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<ProfileDTO> profile(Authentication authentication) {
        String register_no = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByRegisterNumber(register_no);
        Account account = optionalAccount.get();
        ProfileDTO profileDTO = new ProfileDTO(
            account.getId(),
            account.getRegisterNo(),
            account.getAcademicYear(),
            account.getAge(),
            account.getBranch(),
            account.getDepartment(),
            account.getSection(),
            account.getMobile_no(),
            account.getEvents_attended(),
            account.getEmail(),
            account.getAuthorities(),
            account.getCoordinator() != null ? account.getCoordinator().getName() : null);

    return ResponseEntity.ok(profileDTO);

    }

    @GetMapping(value = "/professor/profile", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "View Professor Profile")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Unauthorized")
    @Operation(summary = "View Professor Profile")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<ProfessorProfileDTO> professorProfile(Authentication authentication) {
        String registerNo = authentication.getName();
        Optional<ProfessorAccount> optionalProfessor = professorService.findByRegisterNumber(registerNo);

        if (optionalProfessor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ProfessorAccount professor = optionalProfessor.get();

        ProfessorProfileDTO profileDTO = new ProfessorProfileDTO(
                professor.getId(),
                professor.getRegisterNo(),
                professor.getName(),
                professor.getDesignation(),
                professor.getEmail(),
                professor.getDepartment(),
                professor.getBranch(),
                professor.getAge()
        );

        return ResponseEntity.ok(profileDTO);
    }

  

 

}
