package com.darum.ems.auth.api;

import com.darum.ems.auth.ExceptionHandler.EmailNotFoundException;
import com.darum.ems.auth.ExceptionHandler.UserNotFoundException;
import com.darum.ems.auth.Implementation.ServiceInterface.AuthService;
import com.darum.ems.auth.api.dto.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private final AuthService authenticationService;


    @PostMapping("/login")
    public ResponseEntity <?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
        System.out.println("Has errors?" + result.hasErrors());
        if (result.hasErrors()){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        try {
            return new ResponseEntity<>(authenticationService.loginIn(loginRequest), HttpStatus.ACCEPTED);
        }catch (IllegalArgumentException | UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (EmailNotFoundException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Boolean>> findUser(@PathVariable String email) {
        try {
            Map<String, Boolean> emailExists = authenticationService.checkMail(email);
            return new ResponseEntity<>(emailExists, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
