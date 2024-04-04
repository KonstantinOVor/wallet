package com.example.service.wallet.validation;

import com.example.service.wallet.model.OperationType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumNamePatternValidator implements ConstraintValidator<EnumNamePattern, String> {
    @Override
    public boolean isValid(String type, ConstraintValidatorContext constraintValidatorContext) {
        if (type == null) {
            return false;
        }
        return isValidOperationType(type);
    }

    private boolean isValidOperationType(String value) {
        for(OperationType type : OperationType.values()) {
            if(type.toString().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

}
