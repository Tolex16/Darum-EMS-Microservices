package com.darum.ems.auth.Implementation.ServiceInterface;


import com.darum.ems.auth.api.dto.LoginRequest;
import com.darum.ems.auth.api.dto.LoginResponse;

import java.util.Map;

public interface AuthService {

    void createUser(String email);
    LoginResponse loginIn(LoginRequest loginRequest);

    Map<String, Boolean> checkMail(String email);

}
