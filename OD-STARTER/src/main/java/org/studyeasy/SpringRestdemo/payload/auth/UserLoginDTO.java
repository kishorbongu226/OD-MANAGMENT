package org.studyeasy.SpringRestdemo.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {
   
    @NotBlank(message="register number required")
    @Schema(description = "Register number", example = "A1001", requiredMode = RequiredMode.REQUIRED)
    private String register_no;

    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "password", 
    requiredMode = RequiredMode.REQUIRED, maxLength = 20, minLength = 6)
    private String password;

    
}
