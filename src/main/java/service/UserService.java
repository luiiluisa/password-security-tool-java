package service;

import domain.PasswordEvaluationResult;
import domain.User;
import repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class UserService {
    private final UserRepository userRepository;
    private final PasswordCheckerService checkerService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    public UserService(UserRepository userRepository, PasswordCheckerService checkerService) {
        this.userRepository = userRepository;
        this.checkerService = checkerService;
    }

    public PasswordEvaluationResult evaluatePassword(String username, String birthDate, String password) {
        return checkerService.evaluatePassword(username, birthDate, password);
    }

    public boolean isValidBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isBlank()) {
            return false;
        }

        try {
            LocalDate.parse(birthDate, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void signUp(String username, String birthDate, String password) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("Numele utilizatorului nu poate fi gol.");
        }

        if (!isValidBirthDate(birthDate)) {
            throw new RuntimeException("Data nașterii trebuie să fie validă și în format dd-MM-yyyy.");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Parola nu poate fi goală.");
        }

        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            throw new RuntimeException("Există deja un utilizator cu acest nume.");
        }

        String hash = checkerService.hashPassword(password);
        User user = new User(username, birthDate, hash);
        userRepository.save(user);
    }

    public boolean login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }

        String hash = checkerService.hashPassword(password);
        return hash.equals(user.getPasswordHash());
    }
}