package sample.views;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

import sample.mainDatabase;

public class addCustomerController implements Initializable {

    @FXML
    private TextField customer_name_field;
    @FXML
    private TextField address_1_field;
    @FXML
    private TextField address_2_field;
    @FXML
    private TextField postal_code_field;
    @FXML
    private TextField phone_number_field;
    @FXML
    private ComboBox city_field;
    @FXML
    private TextField country_field;
    @FXML
    private Button add_customer_button;
    @FXML
    private Button cancel_button;

    public void initialize(URL url, ResourceBundle rb){

        // Add items to city drop down menu
        List<String> cities = new ArrayList<String>();
        for (int i=0; i<mainDatabase.cityList.size(); i++){
            cities.add(mainDatabase.cityList.get(i).getCityName());
        }
        ObservableList city_list = FXCollections.observableList(cities);
        city_field.getItems().clear();
        city_field.setItems(city_list);
    }
    
    boolean checkFields(){
        if (customer_name_field.getText() == null || customer_name_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a customer name.");
            return true;
        }
        if (address_1_field.getText() == null || address_1_field.getText().trim().isEmpty() ||
                address_2_field.getText() == null || address_2_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a full address.");
            return true;
        }
        if (phone_number_field.getText() == null || phone_number_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a phone number.");
            return true;
        }
        if(Pattern.matches("[-0-9]+", phone_number_field.getText())==false){
            fieldsAlert("Only numbers and dashes are allowed for phone numbers.");
                    return true;
        }
                if (postal_code_field.getText() == null || postal_code_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a postal code.");
            return true;
        }
        if(Pattern.matches("[-0-9]+", postal_code_field.getText())==false){
            fieldsAlert("Only numbers and dashes are allowed for postal codes.");
                    return true;
        }
        if (city_field.getSelectionModel().isEmpty()) {
            fieldsAlert("Please provide a city.");
            return true;
        }
        if (country_field.getText() == null || country_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a phone number.");
            return true;
        }
        return false;
    }
    
    void fieldsAlert(String message){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.NONE);
            alert.setTitle(message);
            alert.setHeaderText(message);
            alert.setContentText(message);
            Optional<ButtonType> result = alert.showAndWait();
    }

    @FXML
    private void handleCityChoice(ActionEvent event){
        // This function will automatically fill the country field based on city selected
        String city_choice = city_field.getSelectionModel().getSelectedItem().toString();
        int city_country_id;

        for (int i=0; i<mainDatabase.cityList.size(); i++){
            if (mainDatabase.cityList.get(i).getCityName() == city_choice){
                city_country_id = mainDatabase.cityList.get(i).getCountryId();
                for (int x=0; x<mainDatabase.countryList.size(); x++){
                    if (mainDatabase.countryList.get(x).getCountryId() == city_country_id){
                        country_field.setText(mainDatabase.countryList.get(x).getCountryName());
                    }
                }
            }
        }
    }

    @FXML
    void handleAddCustomer(ActionEvent event) throws IOException{
        boolean error = checkFields();
        if(error){
            return;
        }
        mainDatabase.insertNewCustomer(customer_name_field.getText(), address_1_field.getText(), address_2_field.getText(), city_field.getSelectionModel().getSelectedIndex() + 1, postal_code_field.getText(), phone_number_field.getText());
        Parent customersScreen = FXMLLoader.load(getClass().getResource("/sample/views/customers_screen.fxml"));
        Scene scene = new Scene(customersScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    @FXML
    void handleCancel(ActionEvent event) throws IOException{
        Parent customersScreen = FXMLLoader.load(getClass().getResource("/sample/views/customers_screen.fxml"));
        Scene scene = new Scene(customersScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

}
