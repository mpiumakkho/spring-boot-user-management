package com.mp.core.validation;

import org.apache.commons.lang3.StringUtils;

import com.mp.core.dto.UserRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameOnCreateValidator implements ConstraintValidator<ValidateUsernameOnCreate, UserRequestDTO> {
    
    @Override
    public void initialize(ValidateUsernameOnCreate constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserRequestDTO value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        // If userId is not provided (create operation), username is required
        if (StringUtils.isBlank(value.getUserId())) {
            return StringUtils.isNotBlank(value.getUsername());
        }
        
        // For update operations (userId is provided), username is optional
        return true;
    }
} 