package ru.ls.pjwt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;
import ru.ls.pjwt.utils.constants.Validations;

@Size(message = Validations.size)
@ValidationMetaData
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
