package sample.views;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.Customer;
import sample.mainDatabase;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class customersController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private Button add_customer_button;
    @FXML
    private Button modify_customer_button;
    @FXML
    private Button delete_customer_button;
    @FXML
    private Button main_menu_button;
    @FXML
    private TableColumn<Customer, String> customerIdCol;
    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String> customerPhoneNumberCol;
    @FXML
    private TableColumn<Customer, String> customerAddressCol;
    @FXML
    private TableColumn<Customer, String> customerAddressCol2;
    @FXML
    private TableColumn<Customer, String> customerCityCol;
    @FXML
    private TableColumn<Customer, String> customerZipCodeCol;
    @FXML
    private TableColumn<Customer, String> customerCountryCol;


    public void initialize(URL url, ResourceBundle rb) {
        mainDatabase.getCustomerList();
        //lambda expressions are more efficient in this case than using another method to populate the data
        //the alternative is PropertyValueFactory, which does not check at compile time if the
        //correct value will be returned (it happens at run-time instead); lambda expressions will check
        //at compile-time.
        customerIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerId()));
        customerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        customerAddressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        customerAddressCol2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress2()));
        customerCityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCity()));
        customerCountryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCountry()));
        customerZipCodeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPostalCode()));
        customerPhoneNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));

        populateCustomerTable();

    }
    public void populateCustomerTable() {
        customerTable.setItems(mainDatabase.customerList);
    }

    @FXML
    void handleAddCustomer(ActionEvent event) throws IOException {
        Parent addCustomerScreen = FXMLLoader.load(getClass().getResource("/sample/views/add_customer_screen.fxml"));
        Scene scene = new Scene(addCustomerScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    @FXML
    void handleModifyCustomer(ActionEvent event) throws IOException {
        Customer customerToModify;
        customerToModify = customerTable.getSelectionModel().getSelectedItem();
        String customerId = customerToModify.getCustomerId();

        // Construct loader differently so that we can pass the customerID to the next window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/views/modify_customer_screen.fxml"));
        Parent modifyCustomerScreen = loader.load();
        modifyCustomerController controller = loader.getController();
        controller.setFields(customerId);
        Scene scene = new Scene(modifyCustomerScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();

    }

    @FXML
    void handleDeleteCustomer(ActionEvent event) throws IOException {
        Customer customerToDelete;
        customerToDelete = customerTable.getSelectionModel().getSelectedItem();
        String customerId = customerToDelete.getCustomerId();
        mainDatabase.deleteCustomer(customerId);
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
