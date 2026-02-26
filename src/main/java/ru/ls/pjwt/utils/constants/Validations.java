package ru.ls.pjwt.utils.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Validations {
    public final String email = "must be email", blank = "must be not blank";
    public final String size = "size must be between {min} and {max}";
    public final int min = 3, max = 48;
}
