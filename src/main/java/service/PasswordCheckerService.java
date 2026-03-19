package service;

import domain.PasswordEvaluationResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordCheckerService {

    private final List<String> commonPatterns = List.of(
            "123", "1234", "12345", "123456", "qwerty", "password",
            "admin", "test", "abc", "0000", "1111"
    );

    private final List<String> commonPasswords = List.of(
            "password", "admin", "test", "qwerty", "123456",
            "parola", "password123", "admin123"
    );

    public PasswordEvaluationResult evaluatePassword(String username, String birthDate, String password) {
        int score = 0;
        List<String> feedback = new ArrayList<>();

        String passwordLower = password.toLowerCase();
        String usernameLower = username == null ? "" : username.toLowerCase().trim();

        if (password.length() >= 12) {
            score += 2;
        } else if (password.length() >= 8) {
            score += 1;
            feedback.add("Folosește cel puțin 12 caractere.");
        } else {
            feedback.add("Folosește cel puțin 8 caractere, ideal 12 sau mai mult.");
        }

        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            score += 1;
        } else {
            feedback.add("Adaugă cel puțin o literă mare.");
        }

        if (Pattern.compile("[a-z]").matcher(password).find()) {
            score += 1;
        } else {
            feedback.add("Adaugă cel puțin o literă mică.");
        }

        if (Pattern.compile("\\d").matcher(password).find()) {
            score += 1;
        } else {
            feedback.add("Adaugă cel puțin o cifră.");
        }

        if (Pattern.compile("[!@#$%^&*()_\\-+=<>?/]").matcher(password).find()) {
            score += 1;
        } else {
            feedback.add("Adaugă cel puțin un simbol.");
        }

        if (!usernameLower.isEmpty() && passwordLower.contains(usernameLower)) {
            score -= 2;
            feedback.add("Nu folosi numele utilizatorului în parolă.");
        }

        if (birthDate != null && !birthDate.isBlank()) {
            String onlyDigitsFromDate = birthDate.replaceAll("[^0-9]", "");
            if (!onlyDigitsFromDate.isEmpty() && password.replaceAll("[^0-9]", "").contains(onlyDigitsFromDate)) {
                score -= 2;
                feedback.add("Nu folosi data nașterii în parolă.");
            }

            String year = "";
            if (onlyDigitsFromDate.length() >= 4) {
                year = onlyDigitsFromDate.substring(onlyDigitsFromDate.length() - 4);
            }
            if (!year.isEmpty() && password.contains(year)) {
                score -= 2;
                feedback.add("Nu folosi anul nașterii în parolă.");
            }
        }

        if (containsCommonPattern(password)) {
            score -= 2;
            feedback.add("Evită tiparele comune, cum ar fi 123, qwerty, admin.");
        }

        if (commonPasswords.contains(passwordLower)) {
            score -= 3;
            feedback.add("Alege o parolă diferită de cele foarte comune.");
        }

        if (password.chars().distinct().count() <= 3) {
            score -= 2;
            feedback.add("Folosește o diversitate mai mare de caractere.");
        }

        String strength;
        if (score <= 2) {
            strength = "Weak";
        } else if (score <= 4) {
            strength = "Medium";
        } else {
            strength = "Strong";
        }

        return new PasswordEvaluationResult(strength, score, feedback);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Eroare la hash-uirea parolei.", e);
        }
    }

    private boolean containsCommonPattern(String password) {
        String lower = password.toLowerCase();
        for (String pattern : commonPatterns) {
            if (lower.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
}