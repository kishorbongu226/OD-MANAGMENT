// package org.studyeasy.SpringRestdemo.controller;

// import java.util.Optional;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.studyeasy.SpringRestdemo.model.Account;
// import org.studyeasy.SpringRestdemo.model.ProfessorAccount;
// import org.studyeasy.SpringRestdemo.payload.auth.TokenDTO;
// import org.studyeasy.SpringRestdemo.payload.auth.UserLoginDTO;
// import org.studyeasy.SpringRestdemo.service.AccountService;
// import org.studyeasy.SpringRestdemo.service.ProfessorService;
// import org.studyeasy.SpringRestdemo.service.TokenService;
// import org.studyeasy.SpringRestdemo.util.constants.AccountError;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.extern.slf4j.Slf4j;

// @Controller
// @RequestMapping("/api/v1/auth")
// @Tag(name = "Auth Controller", description = "Controller for Account management")
// @Slf4j
// public class AuthController {
//   Logger logger = LoggerFactory.getLogger(getClass());
//     @Autowired
//     private AuthenticationManager authenticationManager;

//     @Autowired
//     private TokenService tokenService;

//     @Autowired
//     private AccountService accountService;

//     @Autowired
//     private ProfessorService professorService;

//     @PostMapping("/token")
//     @ResponseStatus(HttpStatus.OK)
//     public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
//         try {
//             Authentication authentication = authenticationManager
//                     .authenticate(
//                             new UsernamePasswordAuthenticationToken(userLogin.getRegister_no(), userLogin.getPassword()));
//             return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
//         } catch (Exception e) {
//             log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
//             return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
//         }
//     }
//     @GetMapping(value = "/student/profile", produces = "application/json")

//     @Operation(summary = "View profile")
//     @SecurityRequirement(name = "studyeasy-demo-api")
//     public String profile(Model model) {
//         String register_no = "43111437";
//         Optional<Account> optionalAccount = accountService.findByRegisterNumber(register_no);
//         Account account = optionalAccount.get();
//         model.addAttribute("profile", account);
        

//     return "studentProfile";

//     }

//     @GetMapping(value = "/professor/profile", produces = "application/json")
 
   
//     @SecurityRequirement(name = "studyeasy-demo-api")
//     public String professorProfile(Model model) {
//     logger.info("📌 Entered professorProfile() endpoint");

//     String registerNo = "A1001";
//     logger.debug("Looking up professor with register number: {}", registerNo);

//     Optional<ProfessorAccount> optionalProfessor = professorService.findByRegisterNumber(registerNo);

//     if (optionalProfessor.isEmpty()) {
//         logger.warn("⚠️ No professor found with register number: {}", registerNo);
//         // You could redirect or return an error page
//         return "errorPage";
//     }

//     ProfessorAccount professor = optionalProfessor.get();
//     logger.info("✅ Professor found: {}", professor.getName());

//     model.addAttribute("profile", professor);
//     logger.debug("Profile object added to model: {}", professor);

//     logger.info("➡️ Returning view: adminProfile");
//     return "teacherProfile";
//     }

  

 

// }
