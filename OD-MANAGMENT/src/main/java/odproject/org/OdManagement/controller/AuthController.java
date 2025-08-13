package odproject.org.OdManagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    

    @PostMapping("/token")
    public String token()
    {
        return "token";
    }
    
}
