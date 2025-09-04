package org.studyeasy.SpringRestdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.model.ProfessorAccount;
import org.studyeasy.SpringRestdemo.repository.AccountRepository;
import org.studyeasy.SpringRestdemo.repository.ProfessorRepository;
import org.studyeasy.SpringRestdemo.service.AccountService;
import org.studyeasy.SpringRestdemo.service.ProfessorService;
import org.studyeasy.SpringRestdemo.util.constants.Authority;

@Component
public class SeedData implements CommandLineRunner{

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProfessorService professorService;
    @Autowired
    private  ProfessorRepository professorRepository;
    @Autowired
    private  AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {

    ProfessorAccount professor1 = new ProfessorAccount();
    if (!professorRepository.existsByRegisterNo("P1001")) {
        professor1.setRegisterNo("P1001");
}
    professor1.setName("Dr. S. Gayathri");
    professor1.setDesignation("Assistant Professor");
    professor1.setEmail("gayathri@univ.com");
    professor1.setDepartment("CSE");
    professor1.setProfName("S.Gayatri");
    professor1.setBranch("BE");
    professor1.setPassword("password");
    professor1.setAge(40);
    professor1.setAuthorities(Authority.TEACHER.toString());
    professorService.save(professor1);

    // Professor 2
    ProfessorAccount professor2 = new ProfessorAccount();
        if (!professorRepository.existsByRegisterNo("P1002")) {
        professor2.setRegisterNo("P1002");
}

    professor2.setName("Dr. R. Kumar");
    professor2.setDesignation("Associate Professor");
    professor2.setEmail("kumar@univ.com");

    professor2.setDepartment("ECE");
    professor2.setProfName("Kumar");
    professor2.setBranch("BE");
    professor2.setAge(45);
    professor2.setPassword("password");
    professor2.setAuthorities(Authority.TEACHER.toString());
    professorService.save(professor2);    
        // Student 1
    Account student1 = new Account();
            if (!accountRepository.existsByRegisterNo("43111437")) {
        student1.setRegisterNo("43111437");
}

student1.setAge(19);
student1.setBranch("BE");
student1.setAcademicYear(2L);
student1.setDepartment("CSE");
student1.setStudentName("Anil Kumar");
student1.setEvents_attended(5L);
student1.setMobile_no("9346229311");
student1.setSection("E4");
student1.setEmail("student1@univ.com");
student1.setPassword("password123");
student1.setAuthorities(Authority.STUDENT.toString());
// Assign coordinator later once professors are created
student1.setCoordinator(professor1);
accountService.save(student1);

// Student 2
Account student2 = new Account();
if (!accountRepository.existsByRegisterNo("43111438")) {
student1.setRegisterNo("43111438");
}

student2.setAge(20);
student2.setBranch("BE");
student2.setAcademicYear(3L);
student2.setDepartment("ECE");
student2.setEvents_attended(3L);
student2.setMobile_no("9346229312");
student2.setStudentName("Sahil Babu");
student2.setSection("B2");
student2.setEmail("student2@univ.com");
    student2.setPassword("password123");
    student2.setAuthorities(Authority.STUDENT.toString());
    student2.setCoordinator(professor2);
    accountService.save(student2);

    Account student3 = new Account();
    if (!accountRepository.existsByRegisterNo("43111439")) {
    student1.setRegisterNo("43111439");
    }
    
    student3.setBranch("BE");
    student3.setAcademicYear(1L);
    student3.setDepartment("ECE");
    student3.setEvents_attended(3L);
    student3.setMobile_no("9346229312");
    student3.setStudentName("Sai Kumar");
    student3.setSection("B2");
    student3.setEmail("student2@univ.com");
    student3.setPassword("password123");
    student3.setAuthorities(Authority.STUDENT.toString());
    student3.setCoordinator(professor2);
    accountService.save(student3);

    // Professor 1 (Coordinator)
   

    ProfessorAccount admin = new ProfessorAccount();
           if (!professorRepository.existsByRegisterNo("A1001")) {
        admin.setRegisterNo("A1001");

    
    admin.setName("System Admin");
    admin.setDesignation("Administrator");
    admin.setEmail("admin@univ.com");
    admin.setDepartment("Administration");
    admin.setProfName("Lakshman");
    admin.setBranch("N/A");
    admin.setAge(35);
    admin.setPassword("password");
    admin.setAuthorities(Authority.ADMIN.toString());
    professorService.save(admin);


    // Assign Professor Gayathri as coordinator to student1

    // Assign Professor Kumar as coordinator to student2

        
    }
    
}

}