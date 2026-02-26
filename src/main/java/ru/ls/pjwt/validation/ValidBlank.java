package ru.ls.pjwt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import ru.ls.pjwt.utils.constants.Validations;

@NotBlank(message = Validations.blank)
@ValidationMetaData
@Constraint(validatedBy = {})
public @interface ValidBlank {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
 }
