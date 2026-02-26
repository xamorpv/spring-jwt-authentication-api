package ru.ls.pjwt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import ru.ls.pjwt.utils.constants.Validations;

@ValidBlank
@ValidSize
@ValidationMetaData
@Constraint(validatedBy = {})
public @interface ValidString {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @OverridesAttribute(constraint = ValidSize.class, name = "min")
    int min() default Validations.min;

    @OverridesAttribute(constraint = ValidSize.class, name = "max")
    int max() default Validations.max;
}
