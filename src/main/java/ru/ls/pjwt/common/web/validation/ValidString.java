package ru.ls.pjwt.common.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidBlank
@ValidSize
@Constraint(validatedBy = {})
public @interface ValidString {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @OverridesAttribute(constraint = ValidSize.class, name = "min")
    int min() default ValidationConstants.DEFAULT_MIN_STRING_LENGTH;

    @OverridesAttribute(constraint = ValidSize.class, name = "max")
    int max() default ValidationConstants.DEFAULT_MAX_STRING_LENGTH;
}
