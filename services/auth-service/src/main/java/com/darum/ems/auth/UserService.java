package com.darum.ems.auth;


import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService {
    UserDetailsService userDetailsService();
}

