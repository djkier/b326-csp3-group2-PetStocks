package com.joysistvi.petstocks.utility;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public final class PasswordUtility {
    private static final Pattern BCRYPT_HASH_PATTERN = Pattern.compile(
            "^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    private PasswordUtility() {
    }

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || !isBcryptHash(storedHash)) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isBcryptHash(String value) {
        return value != null && BCRYPT_HASH_PATTERN.matcher(value).matches();
    }
}
