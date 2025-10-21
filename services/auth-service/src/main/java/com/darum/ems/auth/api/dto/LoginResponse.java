package com.darum.ems.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginResponse {

    @JsonProperty("token")
    private final String token;

    private final String role;
}
