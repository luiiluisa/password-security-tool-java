package service;

import java.security.SecureRandom;

public class PasswordGeneratorService {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_-+=<>?";
    private static final String ALL = UPPERCASE + LOWERCASE + DIGITS + SYMBOLS;

    private final SecureRandom random = new SecureRandom();

    public String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Lungimea parolei trebuie să fie de cel puțin 8 caractere.");
        }

        StringBuilder password = new StringBuilder();
        password.append(randomChar(UPPERCASE));
        password.append(randomChar(LOWERCASE));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SYMBOLS));

        for (int i = 0; i < length - 4; i++) {
            password.append(randomChar(ALL));
        }

        return shuffle(password.toString());
    }

    private char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    private String shuffle(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}