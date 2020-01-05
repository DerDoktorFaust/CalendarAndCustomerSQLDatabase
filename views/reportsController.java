package sample.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import sample.mainDatabase;

public class reportsController implements Initializable {


    @FXML
    private TextArea typeByMonth_text_area;
    @FXML
    private TextArea consultant_schedules_text_area;
    @FXML
    private TextArea customer_appointments_text_area;
    @FXML
    private Button main_menu_button;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        appointmentTypeByMonth();
        consultantSchedules();
        customerTotalAppointments();
    }

    public void appointmentTypeByMonth() {
        try {
            Statement statement = mainDatabase.getCurrentConnection().createStatement();

            String query = "SELECT type, MONTHNAME(start) as 'Month', COUNT(*) as 'Total' FROM appointment GROUP BY type, MONTH(start)";
            ResultSet results = statement.executeQuery(query);

            String finalText = "";

            while(results.next()){
                String Month = results.getString("Month");
                String Type = results.getString("Type");
                String Total = results.getString("Total");
                finalText = finalText + "\n" + Type + " " + Month + " " + Total;
            }

            typeByMonth_text_area.setText(finalText);

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public void consultantSchedules() {
        try {
            Statement statement = mainDatabase.getCurrentConnection().createStatement();
            String query = "SELECT user.userName, appointment.type, customer.customerName, start, end " +
                    "FROM appointment INNER JOIN customer ON customer.customerId = appointment.customerId" +
                    " INNER JOIN user ON user.userId = appointment.userId " +
                    "GROUP BY user.userName, MONTH(start), start";

            ResultSet results = statement.executeQuery(query);

            String finalText = "Consultant         Customer          Type           Start                          End";

            while(results.next()){
                String consultant = results.getString("userName");
                String customer = results.getString("customerName");
                String type = results.getString("type");
                String start = results.getString("start");
                String end = results.getString("end");
                start = mainDatabase.convertUTCToLocal(start);
                end = mainDatabase.convertUTCToLocal(end);

                finalText = finalText + "\n" + consultant + "                  " + customer + "           " + type + "     " + start + "            " + end;
            }
            consultant_schedules_text_area.setText(finalText.toString());
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public void customerTotalAppointments() {
        try {
            Statement statement = mainDatabase.getCurrentConnection().createStatement();
            String query = "SELECT customer.customerName, COUNT(*) as 'Total' FROM customer JOIN appointment " +
                    "ON customer.customerId = appointment.customerId GROUP BY customerName";
            ResultSet results = statement.executeQuery(query);

            String finalText = "";
            while(results.next()){
                String customer = results.getString("customerName");
                String total = results.getString("Total");

                finalText = finalText + "\n" + customer + " " + total;
            }
            customer_appointments_text_area.setText(finalText.toString());
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }



    @FXML
    void handleMainMenu(ActionEvent event) throws IOException {
        Parent mainMenuScreen = FXMLLoader.load(getClass().getResource("/sample/views/main_menu_screen.fxml"));
        Scene scene = new Scene(mainMenuScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
