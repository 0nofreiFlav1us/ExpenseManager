package com.onofreiflavius.expenseManager.controller.sessions;

import org.apache.commons.lang3.RandomStringUtils;


public class SessionTokenGenerator {

    private final static String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

    public static String generateToken() {
        return RandomStringUtils.random(35, charSet);
    }

}
