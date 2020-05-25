package com.github.bluecatlee.common.valid.customize;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    String REGEXP_PHONE = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        boolean flag = value.matches(REGEXP_PHONE);
        return flag;
    }

}
