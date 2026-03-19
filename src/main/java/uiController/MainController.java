package uiController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    public void handleOpenLogin(ActionEvent event) {
        openWindow("/view/login-view.fxml", "Log In", event);
    }

    public void handleOpenSignUp(ActionEvent event) {
        openWindow("/view/signup-view.fxml", "Sign Up", event);
    }

    private void openWindow(String fxml, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 700, 500));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}