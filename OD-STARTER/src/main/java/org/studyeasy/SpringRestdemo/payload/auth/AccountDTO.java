package org.studyeasy.SpringRestdemo.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountDTO {

    @NotBlank(message="register number required")
    @Schema(description = "Register number", example = "43111***", requiredMode = RequiredMode.REQUIRED)
    private String register_no;

    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "Password", 
    requiredMode = RequiredMode.REQUIRED, maxLength = 20, minLength = 6)
    private String password;
    
}
