package sample.views;
//import com.sun.org.apache.xml.internal.security.Init;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;


public class mainMenuController {
    @FXML
    private Button appointments_button;
    @FXML
    private Button reports_button;
    @FXML
    private Button customers_button;


    @FXML
    void handleCustomers(ActionEvent event) throws IOException {
        Parent customersScreen = FXMLLoader.load(getClass().getResource("/sample/views/customers_screen.fxml"));
        Scene scene = new Scene(customersScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    @FXML
    void handleAppointments(ActionEvent event) throws IOException {
        Parent appointmentsScreen = FXMLLoader.load(getClass().getResource("/sample/views/appointments_screen.fxml"));
        Scene scene = new Scene(appointmentsScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    @FXML
    void handleReports(ActionEvent event) throws IOException{
        Parent appointmentsScreen = FXMLLoader.load(getClass().getResource("/sample/views/reports_screen.fxml"));
        Scene scene = new Scene(appointmentsScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
