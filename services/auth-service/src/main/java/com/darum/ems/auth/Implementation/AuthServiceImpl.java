package com.darum.ems.auth.Implementation;

import com.darum.ems.auth.ExceptionHandler.EmailNotFoundException;
import com.darum.ems.auth.ExceptionHandler.UserNotFoundException;
import com.darum.ems.auth.Implementation.ServiceInterface.AuthService;
import com.darum.ems.auth.Implementation.ServiceInterface.JwtService;
import com.darum.ems.auth.api.dto.LoginRequest;
import com.darum.ems.auth.api.dto.LoginResponse;
import com.darum.ems.auth.domain.Role;
import com.darum.ems.auth.domain.User;
import com.darum.ems.auth.domain.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void createUser(String email) {
        // Check if the user already exists
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("User already exists" + email);
        }

        // Create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(generateRandomPassword()));

        newUser.setRole(Role.EMPLOYEE);
        userRepository.save(newUser);
    }

    public static String generateRandomPassword() {
        PasswordGenerator generator = new PasswordGenerator();

        // Define password rules
        List<CharacterRule> rules = Arrays.asList(
            new CharacterRule(EnglishCharacterData.UpperCase, 2),
            new CharacterRule(EnglishCharacterData.LowerCase, 3),
            new CharacterRule(EnglishCharacterData.Digit, 2),
            new CharacterRule(EnglishCharacterData.Special, 1)
        );

        // Remove confusing characters for better readability
        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() { return "ERROR_SPECIAL"; }
            public String getCharacters() { return "!@#$%^&*()_+-=[]{}"; }
        };

        // Add the special characters rule
        List<CharacterRule> finalRules = new ArrayList<>(rules);
        finalRules.add(new CharacterRule(specialChars, 2));

        // Generate a strong 10–12 character password
        return generator.generatePassword(8, finalRules);
    }

  public LoginResponse loginIn(LoginRequest loginRequest) {
    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          loginRequest.getEmail(), loginRequest.getPassword()
        )
      );
    } catch (BadCredentialsException e) {
      throw new EmailNotFoundException("Invalid email or password");
    }

    var user = userRepository.findByEmailIgnoreCase(loginRequest.getEmail())
      .orElseThrow(() -> new UserNotFoundException("User does not exist"));

    userRepository.save(user); // <-- This is required to persist changes

    // Generate token and map user
    String jwt = jwtService.generateToken(user);

    return new LoginResponse(jwt);
  }

  @Transactional
  public Map<String, Boolean> checkMail(String email) {
    Map<String, Boolean> result = new HashMap<>();

    boolean isEmailPresent = userRepository.findByEmailIgnoreCase(email).isPresent();
    result.put("email", isEmailPresent);

    return result;
  }

  @PostConstruct
  public void createAdminUsers() {
    Optional<User> adminUser = userRepository.findByEmailIgnoreCase("darumems@gmail.com");
    if (adminUser.isEmpty()) {
      User createAdmin = new User();
      createAdmin.setEmail("darumems@gmail.com");
      createAdmin.setPassword(passwordEncoder.encode("Winner123!"));
      createAdmin.setRole(Role.ADMIN);
      userRepository.save(createAdmin);
    }
  }

}
