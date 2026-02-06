package ru.ls.pjwt.utils.constants;

import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKey;

@UtilityClass
public class Jwt {
    // todo нормально хранить secret key
    public final SecretKey secretKey =
            Keys.hmacShaKeyFor("j*&(fIHf745/l;IF*y(qpigimvauq&$hF(*&4JF&*;4*fy:1879$%8FGY;/g8jhp*t[pAKLJKOP:f8A4:f87Y)*&%^%()w$;'F*ygaidcjvkJN;alK4R85YFz{GB1R=[Edvf']".getBytes()); // небезопасно, но это учебный проект

    public final String ACCESS = "access", REFRESH = "refresh";
    public final int accessTokenExpirationMinutes = 10;
    public final int refreshTokenExpirationDays = 14;

}
