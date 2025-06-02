package com.mp.core.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameOnCreateValidator.class)
public @interface ValidateUsernameOnCreate {
    String message() default "Username is required when creating a new user";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 