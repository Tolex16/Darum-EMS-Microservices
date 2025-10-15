package com.darum.ems.auth.Config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])(?!.*\\s).{8,}$", message = "Invalid password format")
public @interface StrongPassword {
    String message() default "Invalid password format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
