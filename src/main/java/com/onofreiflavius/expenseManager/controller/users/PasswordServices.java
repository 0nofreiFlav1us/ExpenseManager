package com.onofreiflavius.expenseManager.controller.users;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordServices {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static Boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

}
