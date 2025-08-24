package org.studyeasy.SpringRestdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.service.AccountService;
import org.studyeasy.SpringRestdemo.util.constants.Authority;

@Component
public class SeedData implements CommandLineRunner{

    @Autowired
    private AccountService accountService;

    @Override
    public void run(String... args) throws Exception {
        Account account01 = new Account();
        Account account02 = new Account();
        Account account03 = new Account();
        // Account account02 = new Account();


        account01.setRegisterNo("43111436");
        account01.setAge(19);
        account01.setBranch("BE");
        account01.setAcademicYear(2);
        account01.setDepartment("CSE");
        account01.setEvents_attended(10);
        account01.setMobile_no("9346229311");
        account01.setCo_ordinator("Dr.S.Gayathri");
        account01.setSection("E4");
        account01.setEmail("user@user.com");
        account01.setPassword("22-04-2006");
        account01.setAuthorities(Authority.ADMIN.toString());
        accountService.save(account01);

        account02.setRegisterNo("43111437");
        account02.setAge(19);
        account02.setBranch("BE");
        account02.setAcademicYear(2);
        account02.setDepartment("CSE");
        account02.setEvents_attended(10);
        account02.setMobile_no("9346229311");
        account02.setCo_ordinator("Dr.S.Gayathri");
        account02.setSection("E4");
        account02.setEmail("user@user.com");
        account02.setPassword("22-04-2006");
        account02.setAuthorities(Authority.STUDENT.toString());
        accountService.save(account02); 

        account03.setRegisterNo("43111438");
        account03.setAge(19);
        account03.setBranch("BE");
        account03.setAcademicYear(2);
        account03.setDepartment("CSE");
        account03.setEvents_attended(10);
        account03.setMobile_no("9346229311");
        account03.setCo_ordinator("Dr.S.Gayathri");
        account03.setSection("E4");
        account03.setEmail("user@user.com");
        account03.setPassword("22-04-2006");
        account03.setAuthorities(Authority.TEACHER.toString());
        accountService.save(account03);

        // account02.setEmail("admin@admin.com");
        // account02.setPassword("pass987");
        // account02.setAuthorities(Authority.ADMIN.toString() +" "+Authority.USER.toString() );
        // accountService.save(account02);
        
    }
    
}
