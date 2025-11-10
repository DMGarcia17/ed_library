package org.dmgarcia.app.infra;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    public static boolean matches(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}
