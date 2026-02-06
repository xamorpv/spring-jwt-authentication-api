package ru.ls.pjwt.utils.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Validations {
    public final String email = "must be email", nn = "must exists", blank = "must be not blank";
    public final String size = "size must be between {min} and {max}", minSize = "size must be greater than {min}", maxSize = "size must be less than {max}";
}
