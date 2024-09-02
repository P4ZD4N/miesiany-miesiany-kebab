package com.p4zd4n.kebab.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordEncoder {

    public static String encodePassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    public static boolean matches(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
