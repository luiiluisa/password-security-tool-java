package uiController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SuccessController {

    @FXML
    private Button backButton;

    @FXML
    private void handleBackHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view.fxml"));
            Scene scene = new Scene(loader.load(), 700, 500);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Password Security Tool");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}