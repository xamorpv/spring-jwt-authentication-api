package ru.ls.pjwt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;
import ru.ls.pjwt.utils.constants.Validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Size(message = Validations.size)
@Constraint(validatedBy = {})
public @interface ValidSize {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @OverridesAttribute(constraint = Size.class, name = "min")
    int min() default Validations.min;

    @OverridesAttribute(constraint = Size.class, name = "max")
    int max() default Validations.max;
}
