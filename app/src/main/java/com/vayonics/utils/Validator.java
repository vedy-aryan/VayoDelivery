package com.vayonics.utils;

public class Validator {
    public static boolean isStrongPassword(String password) {
        // At least 8 chars, 1 digit, 1 letter, 1 special char
        String regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(regex);
    }
}
