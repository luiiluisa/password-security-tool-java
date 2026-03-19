package uiController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;
import repository.UserDBRepository;
import service.PasswordCheckerService;
import service.UserService;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Label messageLabel;

    private UserService userService;
    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        userService = new UserService(new UserDBRepository(), new PasswordCheckerService());

        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
        togglePasswordButton.setText("👁");

        clearMessage();
    }

    @FXML
    private void handleLogin() {
        clearMessage();

        String username = usernameField.getText().trim();
        String password = getCurrentPassword();

        if (username.isEmpty()) {
            showError("Completează numele utilizatorului.");
            return;
        }

        if (password.isEmpty()) {
            showError("Completează parola.");
            return;
        }

        boolean ok = userService.login(username, password);

        if (ok) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/success-view.fxml"));
                Scene scene = new Scene(loader.load(), 700, 500);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Succes");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            showError("Username sau parola sunt incorecte.");
        }
    }

    @FXML
    private void handlePastePassword() {
        clearMessage();

        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String text = clipboard.getString();
            passwordField.setText(text);
            visiblePasswordField.setText(text);
        } else {
            showError("Clipboard-ul nu conține text.");
        }
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

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }
}