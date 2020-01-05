package sample.views;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import sample.User;
import sample.mainDatabase;

public class loginController implements Initializable {

    User currentUser = new User();

    @FXML
    private Label user_name_label;
    @FXML
    private TextField user_name_field;
    @FXML
    private Label password_label;
    @FXML
    private PasswordField password_field;
    @FXML
    private Button login_button;

    @FXML
    void handleLogin(ActionEvent event) throws IOException {

        String provided_user_name = user_name_field.getText();;
        String provided_password = password_field.getText();;
        boolean login = false;


        currentUser.setUsername(user_name_field.getText());
        currentUser.setPassword(password_field.getText());

        login = currentUser.attemptLogin();

        if (login == false){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.NONE);
            alert.setTitle("Incorrect Login");
            alert.setHeaderText("Login Failed");
            alert.setContentText("Incorrect username and password combination");
            Optional<ButtonType> result = alert.showAndWait();
            return;
        } else {
            mainDatabase.getCustomerList();
            mainDatabase.getAppointmentList();
            mainDatabase.checkForImminentAppointment();
            Parent mainMenuScreen = FXMLLoader.load(getClass().getResource("/sample/views/main_menu_screen.fxml"));
            Scene scene = new Scene(mainMenuScreen);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Locale locale = Locale.getDefault();

        ResourceBundle languageResources = ResourceBundle.getBundle("sample.languages/login", locale);

        password_label.setText(languageResources.getString("password"));
        user_name_label.setText(languageResources.getString("username"));
        login_button.setText(languageResources.getString("login"));
    }
}
