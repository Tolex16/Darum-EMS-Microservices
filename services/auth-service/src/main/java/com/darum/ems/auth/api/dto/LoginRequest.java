package com.darum.ems.auth.api.dto;

import com.darum.ems.auth.Config.StrongPassword;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Email should be valid")
    private final String email;

    @StrongPassword
    private final String password;
}
