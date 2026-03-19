package uiController;

import domain.PasswordEvaluationResult;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import repository.UserDBRepository;
import service.PasswordCheckerService;
import service.PasswordGeneratorService;
import service.UserService;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField birthDateField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private TextArea resultArea;

    @FXML
    private Label strengthLabel;

    @FXML
    private Label messageLabel;

    private UserService userService;
    private PasswordGeneratorService generatorService;

    private boolean passwordVisible = false;

    private boolean passwordVerified = false;
    private boolean passwordAccepted = false;

    private String verifiedUsername = "";
    private String verifiedBirthDate = "";
    private String verifiedPassword = "";

    @FXML
    public void initialize() {
        PasswordCheckerService checkerService = new PasswordCheckerService();
        userService = new UserService(new UserDBRepository(), checkerService);
        generatorService = new PasswordGeneratorService();

        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

        togglePasswordButton.setText("👁");

        clearMessage();
        clearStrength();
        resultArea.setText("Rezultatul va apărea aici.\nFormat data nașterii: dd-MM-yyyy");

        usernameField.textProperty().addListener((obs, oldVal, newVal) -> resetVerification());
        birthDateField.textProperty().addListener((obs, oldVal, newVal) -> resetVerification());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> resetVerification());
        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> resetVerification());
    }

    @FXML
    private void handleVerify() {
        clearMessage();
        clearStrength();

        String username = usernameField.getText().trim();
        String birthDate = birthDateField.getText().trim();
        String password = getCurrentPassword();

        if (username.isEmpty()) {
            showError("Completează numele utilizatorului.");
            return;
        }

        if (!userService.isValidBirthDate(birthDate)) {
            showError("Data nașterii trebuie să fie validă și în format dd-MM-yyyy.");
            return;
        }

        if (password.isEmpty()) {
            showError("Introdu parola.");
            return;
        }

        PasswordEvaluationResult result = userService.evaluatePassword(username, birthDate, password);

        verifiedUsername = username;
        verifiedBirthDate = birthDate;
        verifiedPassword = password;
        passwordVerified = true;
        passwordAccepted = !"Weak".equals(result.getStrength());

        showStrength(result.getStrength());
        showSuggestions(result);

        if (passwordAccepted) {
            showSuccess("Parola a fost verificată. Poți crea contul.");
        } else {
            showError("Parola este Weak. Trebuie să fie cel puțin Medium pentru a crea contul.");
        }
    }

    @FXML
    private void handleGenerate() {
        clearMessage();
        clearStrength();

        String username = usernameField.getText().trim();
        String birthDate = birthDateField.getText().trim();

        if (username.isEmpty()) {
            showError("Completează mai întâi numele utilizatorului.");
            return;
        }

        if (!userService.isValidBirthDate(birthDate)) {
            showError("Completează o dată a nașterii validă înainte de generare. Format: dd-MM-yyyy.");
            return;
        }

        String password = generatorService.generatePassword(14);
        setCurrentPassword(password);

        PasswordEvaluationResult result = userService.evaluatePassword(username, birthDate, password);

        verifiedUsername = username;
        verifiedBirthDate = birthDate;
        verifiedPassword = password;
        passwordVerified = true;
        passwordAccepted = !"Weak".equals(result.getStrength());

        showStrength(result.getStrength());

        StringBuilder text = new StringBuilder();
        text.append("Parolă generată:\n");
        text.append(password).append("\n\n");

        if ("Strong".equals(result.getStrength())) {
            text.append("Parola este deja Strong.");
        } else {
            text.append("Pentru a ajunge la Strong:\n");
            for (String feedback : result.getFeedback()) {
                text.append("- ").append(feedback).append("\n");
            }
        }

        resultArea.setText(text.toString());
        showSuccess("Parola a fost generată și verificată.");
    }

    @FXML
    private void handleSignUp() {
        clearMessage();

        String username = usernameField.getText().trim();
        String birthDate = birthDateField.getText().trim();
        String password = getCurrentPassword();

        if (username.isEmpty()) {
            showError("Completează numele utilizatorului.");
            return;
        }

        if (!userService.isValidBirthDate(birthDate)) {
            showError("Data nașterii trebuie să fie validă și în format dd-MM-yyyy.");
            return;
        }

        if (password.isEmpty()) {
            showError("Completează parola.");
            return;
        }

        if (!passwordVerified) {
            showError("Verifică parola înainte să creezi contul.");
            return;
        }

        if (!username.equals(verifiedUsername) || !birthDate.equals(verifiedBirthDate) || !password.equals(verifiedPassword)) {
            showError("Ai modificat datele după verificare. Verifică parola din nou.");
            return;
        }

        if (!passwordAccepted) {
            showError("Nu poți crea contul. Parola trebuie să fie cel puțin Medium.");
            return;
        }

        try {
            userService.signUp(username, birthDate, password);
            showSuccess("Cont creat cu succes.");
            clearAll();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleCopyPassword() {
        clearMessage();

        String password = getCurrentPassword();
        if (password.isEmpty()) {
            showError("Nu există nicio parolă de copiat.");
            return;
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(password);
        Clipboard.getSystemClipboard().setContent(content);

        showSuccess("Parola a fost copiată.");
    }

    @FXML
    private void handleTogglePassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.setText("👁");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view.fxml"));
            Scene scene = new Scene(loader.load(), 700, 500);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Password Security Tool");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getCurrentPassword() {
        return passwordVisible ? visiblePasswordField.getText().trim() : passwordField.getText().trim();
    }

    private void setCurrentPassword(String password) {
        passwordField.setText(password);
        visiblePasswordField.setText(password);
    }

    private void showSuggestions(PasswordEvaluationResult result) {
        StringBuilder text = new StringBuilder();

        if ("Strong".equals(result.getStrength())) {
            text.append("Parola este deja Strong.");
        } else {
            text.append("Pentru a ajunge la Strong:\n");
            for (String feedback : result.getFeedback()) {
                text.append("- ").append(feedback).append("\n");
            }
        }

        resultArea.setText(text.toString());
    }

    private void showStrength(String strength) {
        strengthLabel.setText("Nivel parolă: " + strength);

        if ("Weak".equals(strength)) {
            strengthLabel.setStyle("-fx-text-fill: #c62828; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else if ("Medium".equals(strength)) {
            strengthLabel.setStyle("-fx-text-fill: #ef6c00; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            strengthLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    private void clearStrength() {
        strengthLabel.setText("");
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }

    private void resetVerification() {
        passwordVerified = false;
        passwordAccepted = false;
        verifiedUsername = "";
        verifiedBirthDate = "";
        verifiedPassword = "";
        clearMessage();
        clearStrength();
    }

    private void clearAll() {
        usernameField.clear();
        birthDateField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
        resultArea.setText("Rezultatul va apărea aici.\nFormat data nașterii: dd-MM-yyyy");
        resetVerification();
    }
}