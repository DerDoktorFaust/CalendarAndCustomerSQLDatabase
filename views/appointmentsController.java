package sample.views;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.Appointment;
import sample.Customer;
import sample.mainDatabase;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import javafx.stage.Modality;

import sample.User;
import static sample.mainDatabase.appointmentList;

public class appointmentsController implements Initializable {

    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, String> customerCol;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeCol;
    @FXML
    private TableColumn<Appointment, String> locationCol;
    @FXML
    private TableColumn<Appointment, String> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private ComboBox customer_combo_box;
    @FXML
    private TextField title_field;
    @FXML
    private TextField description_field;
    @FXML
    private TextField location_field;
    @FXML
    private TextField contact_field;
    @FXML
    private ComboBox type_combo_box;
    @FXML
    private TextField url_field;
    @FXML
    private DatePicker date_selector;
    @FXML
    private ComboBox time_combo_box;
    @FXML
    private ComboBox end_time_combo_box;
    @FXML
    private Button add_button;
    @FXML
    private Button modify_button;
    @FXML
    private Button delete_button;
    @FXML
    private Button clear_button;
    @FXML
    private Button main_menu_button;
    @FXML
    private Button transfer_data_button;
    @FXML
    private Button view_all_appointments_button;
    @FXML
    private Button view_week_button;
    @FXML
    private Button view_month_button;

    Appointment appointmentToTransfer;
    SortedList<Appointment> sortedAppointments = new SortedList<>(mainDatabase.appointmentList);

    public void initialize(URL url, ResourceBundle rb) {
        mainDatabase.getAppointmentList();
        //lambda expressions are more efficient in this case than using another method to populate the data
        //the alternative is PropertyValueFactory, which does not check at compile time if the
        //correct value will be returned (it happens at run-time instead); lambda expressions will check
        //at compile-time.
        customerCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        appointmentTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        startCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart_time()));
        endCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd_time()));



        populateAppointmentTable();

        time_combo_box.getItems().addAll(
                "08:00:00", "08:30:00", "09:00:00", "09:30:00", "10:00:00", "10:30:00",
                "11:00:00", "11:30:00", "12:00:00", "12:30:00", "13:00:00", "13:30:00", "14:00:00",
                "14:30:00", "15:00:00", "15:30:00", "16:00:00", "16:30:00");
        end_time_combo_box.getItems().addAll(
                "08:00:00", "08:30:00", "09:00:00", "09:30:00", "10:00:00", "10:30:00",
                "11:00:00", "11:30:00", "12:00:00", "12:30:00", "13:00:00", "13:30:00", "14:00:00",
                "14:30:00", "15:00:00", "15:30:00", "16:00:00", "16:30:00");
        type_combo_box.getItems().addAll("Presentation", "Scrum", "In-Person", "Phone", "Skype");

        // Add items to city drop down menu
        List<String> customers = new ArrayList<String>();
        for (int i=0; i<mainDatabase.customerList.size(); i++){
            customers.add(mainDatabase.customerList.get(i).getCustomerName());
        }
        ObservableList customer_list = FXCollections.observableList(customers);
        customer_combo_box.getItems().clear();
        customer_combo_box.setItems(customer_list);
    }

    public void populateAppointmentTable() {
        //only sortedlists can be used to sort by column
        //SortedList<Appointment> sortedAppointments = new SortedList<>(mainDatabase.appointmentList);
        sortedAppointments.comparatorProperty().bind(appointmentsTable.comparatorProperty());
        appointmentsTable.setItems(sortedAppointments);
        appointmentsTable.getSortOrder().addAll(startCol);
    }
    
        boolean checkFields(){
        if (customer_combo_box.getSelectionModel().isEmpty()) {
            fieldsAlert("Please provide a customer name.");
            return true;
        }
        if (title_field.getText() == null || title_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a title.");
            return true;
        }
        if (description_field.getText() == null || description_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a description.");
            return true;
        }
        if (location_field.getText() == null || location_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide location information.");
            return true;
        }
        if (contact_field.getText() == null || contact_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide contact information.");
            return true;
        }
        if (type_combo_box.getSelectionModel().isEmpty()) {
            fieldsAlert("Please provide an appointment type.");
            return true;
        }
        if (url_field.getText() == null || url_field.getText().trim().isEmpty()) {
            fieldsAlert("Please provide a URL or type none.");
            return true;
        }
        if (date_selector.getValue() == null) {
            fieldsAlert("Please provide a date.");
            return true;
        }
        if (time_combo_box.getSelectionModel().isEmpty()) {
            fieldsAlert("Please provide an appointment type.");
            return true;
        }
        if (end_time_combo_box.getSelectionModel().isEmpty()) {
            fieldsAlert("Please provide an appointment type.");
            return true;
        }
        return false;
    }
        
    boolean checkOverlappingDates(String start_time, String end_time, int userId, int customerId){
        try{
            Date appointment_start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(appointment_start_time);

            
            Date appointment_end_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(appointment_end_time);

            
        for (int i = 0; i<mainDatabase.appointmentList.size(); i++){

            Date temp_start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mainDatabase.appointmentList.get(i).getStart_time());
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(temp_start_time);

            
            Date temp_end_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mainDatabase.appointmentList.get(i).getEnd_time());
            Calendar calendar4 = Calendar.getInstance();
            calendar4.setTime(temp_end_time);
            
            if (calendar1.getTime().after(calendar2.getTime())){
                fieldsAlert("Your end time needs to be after your start time.");
                return true;
            }
            


          
            if (((appointment_start_time.after(calendar3.getTime()) || appointment_start_time.equals(calendar3.getTime())) 
                    && appointment_start_time.before(calendar4.getTime())) && 
                    ((mainDatabase.appointmentList.get(i).getUserId() == userId || mainDatabase.appointmentList.get(i).getCustomerId() == customerId))) 
     
            {
                fieldsAlert("You have conflicting appointment slots.");
                return true;
            }

            if (appointment_end_time.after(calendar3.getTime()) 
                    && (appointment_end_time.before(calendar4.getTime()) || appointment_end_time.equals(calendar4.getTime()))
                    && ((mainDatabase.appointmentList.get(i).getUserId() == userId) || mainDatabase.appointmentList.get(i).getCustomerId() == customerId)){
                fieldsAlert("You have conflicting appointment slots.");
                return true;
            }
            if(appointment_start_time.before(calendar3.getTime()) 
                    && appointment_end_time.after(calendar4.getTime())
                    && ((mainDatabase.appointmentList.get(i).getUserId() == userId)|| mainDatabase.appointmentList.get(i).getCustomerId() == customerId)){
                                fieldsAlert("You have conflicting appointment slots.");
                return true;
            
            }
        }
        }catch (Exception e){
            return false;
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
    void handleAddAppointment(ActionEvent event) throws IOException {
        boolean error = checkFields();
        if(error){
            return;
        }
        int newCustomerId = -1;
        int newUserId = mainDatabase.getCurrent_user_id();
        String newCustomer = customer_combo_box.getSelectionModel().getSelectedItem().toString();
        String newTitle = title_field.getText();
        String newDescription = description_field.getText();
        String newLocation = location_field.getText();
        String newContact = contact_field.getText();
        String newType = type_combo_box.getSelectionModel().getSelectedItem().toString();
        String newURL = url_field.getText();

        //// Intermediate steps for start and end times
        LocalDate localDate = date_selector.getValue();
        String newDate = localDate.toString();
        String newStartTime = time_combo_box.getSelectionModel().getSelectedItem().toString();
        String newEndTime = end_time_combo_box.getSelectionModel().getSelectedItem().toString();

        // newStartDate and newEndDate are the ones to use
        String newStartDate = newDate + " " + newStartTime;
        String newEndDate = newDate + " " + newEndTime;
        
                //Need to find customer ID
        for (int i=0; i<mainDatabase.customerList.size(); i++){
            if (mainDatabase.customerList.get(i).getCustomerName().equals(newCustomer)){
                newCustomerId = Integer.parseInt(mainDatabase.customerList.get(i).getCustomerId());
            }
        }
        
        error = checkOverlappingDates(newStartDate, newEndDate, newUserId, newCustomerId);
        if(error){
            return;
        }
        
        newStartDate = mainDatabase.convertLocalToUTC(newStartDate); //convert to server time zone
        newEndDate = mainDatabase.convertLocalToUTC(newEndDate); //convert to server time zone
        

        
        mainDatabase.insertNewAppointment(newCustomerId, newUserId, newTitle, newDescription, newLocation,
                newContact, newType, newURL, newStartDate, newEndDate);

        mainDatabase.getAppointmentList();
        populateAppointmentTable();
    }

    @FXML
    void handleModifyAppointment(ActionEvent event) throws IOException{
        boolean error = checkFields();
        if(error){
            return;
        }
        int newCustomerId = -1;
        int newUserId = mainDatabase.getCurrent_user_id();
        String newCustomer = customer_combo_box.getSelectionModel().getSelectedItem().toString();
        String newTitle = title_field.getText();
        String newDescription = description_field.getText();
        String newLocation = location_field.getText();
        String newContact = contact_field.getText();
        String newType = type_combo_box.getSelectionModel().getSelectedItem().toString();
        String newURL = url_field.getText();

        //// Intermediate steps for start and end times
        LocalDate localDate = date_selector.getValue();
        String newDate = localDate.toString();
        String newStartTime = time_combo_box.getSelectionModel().getSelectedItem().toString();
        String newEndTime = end_time_combo_box.getSelectionModel().getSelectedItem().toString();

        // newStartDate and newEndDate are the ones to use
        String newStartDate = newDate + " " + newStartTime;
        String newEndDate = newDate + " " + newEndTime;
        
                //Need to find customer ID
        for (int i=0; i<mainDatabase.customerList.size(); i++){
            if (mainDatabase.customerList.get(i).getCustomerName().equals(newCustomer)){
                newCustomerId = Integer.parseInt(mainDatabase.customerList.get(i).getCustomerId());
            }
        }
        
        error = checkOverlappingDates(newStartDate, newEndDate, newUserId, newCustomerId);
        if(error){
            return;
        }
        
        newStartDate = mainDatabase.convertLocalToUTC(newStartDate); //convert to server time zone
        newEndDate = mainDatabase.convertLocalToUTC(newEndDate); //convert to server time zone


        int newAppointmentId = appointmentToTransfer.getAppointmentId();
        mainDatabase.modifyAppointment(newAppointmentId, newCustomerId, newUserId, newTitle, newDescription, newLocation,
                newContact, newType, newURL, newStartDate, newEndDate);

        mainDatabase.getAppointmentList();
        populateAppointmentTable();

        customer_combo_box.setValue(null);
        title_field.clear();
        description_field.clear();
        location_field.clear();
        contact_field.clear();
        type_combo_box.setValue(null);
        url_field.clear();
        date_selector.setValue(null);
        time_combo_box.setValue(null);
        end_time_combo_box.setValue(null);
        appointmentToTransfer = null;
    }

    @FXML
    void handleDeleteAppointment(ActionEvent event) throws IOException{
        Appointment appointmentToDelete;
        appointmentToDelete = appointmentsTable.getSelectionModel().getSelectedItem();
        int appointmentId = appointmentToDelete.getAppointmentId();
        mainDatabase.deleteAppointment(appointmentId);
        mainDatabase.getAppointmentList();
        populateAppointmentTable();
    }

    @FXML
    void handleClearButton(ActionEvent event) throws IOException{
        customer_combo_box.setValue(null);
        title_field.clear();
        description_field.clear();
        location_field.clear();
        contact_field.clear();
        type_combo_box.setValue(null);
        url_field.clear();
        date_selector.setValue(null);
        time_combo_box.setValue(null);
        end_time_combo_box.setValue(null);
        appointmentToTransfer = null;
    }

    @FXML
    void handleDataTransfer(ActionEvent event) throws IOException{
        appointmentToTransfer = appointmentsTable.getSelectionModel().getSelectedItem();
        customer_combo_box.setValue(appointmentToTransfer.getCustomerName());
        title_field.setText(appointmentToTransfer.getTitle());
        description_field.setText(appointmentToTransfer.getDescription());
        location_field.setText(appointmentToTransfer.getLocation());
        contact_field.setText(appointmentToTransfer.getContact());
        type_combo_box.setValue(appointmentToTransfer.getType());
        url_field.setText(appointmentToTransfer.getUrl());

        try {
            String dateString = appointmentToTransfer.getStart_time();
            dateString = dateString.substring(0,10);
            LocalDate date = LocalDate.parse(dateString);
            date_selector.setValue(date);

            String startTime = appointmentToTransfer.getStart_time();
            startTime = startTime.substring(11, startTime.length());
            time_combo_box.setValue(startTime);

            String endTime = appointmentToTransfer.getEnd_time();
            endTime = endTime.substring(11, endTime.length());
            end_time_combo_box.setValue(endTime);

        } catch (Exception e){
            System.out.println("Error " + e);
        }

    }

    @FXML
    void handleViewAllAppointments(ActionEvent event) throws IOException {
        mainDatabase.getAppointmentList();
        populateAppointmentTable();
    }

    @FXML
    void handleViewWeek(ActionEvent event) throws IOException{
        mainDatabase.getAppointmentList();
        ObservableList<Appointment> appointmentWeekList = FXCollections.observableArrayList(mainDatabase.appointmentList);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String currentDate = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE, 7);
        String futureDate = dateFormat.format(cal.getTime());


        for (int i=0; i<appointmentWeekList.size(); i++){
            //System.out.println(appointmentWeekList.get(i).getStart_time());
            if ((appointmentWeekList.get(i).getStart_time().compareTo(currentDate) < 0) || (appointmentWeekList.get(i).getStart_time().compareTo(futureDate) > 0))
            {
                int id = appointmentWeekList.get(i).getAppointmentId();
                for(int x =0; x<mainDatabase.appointmentList.size(); x++){
                    if (mainDatabase.appointmentList.get(x).getAppointmentId() == id){
                        //System.out.println(mainDatabase.appointmentList.get(x).getStart_time());
                        mainDatabase.appointmentList.remove(x);
                        break;
                    }
                }
            }
        }

        SortedList<Appointment> sortedAppointments = new SortedList<>(mainDatabase.appointmentList);
        populateAppointmentTable();

    }

    @FXML
    void handleViewMonth(ActionEvent event) throws IOException{
        mainDatabase.getAppointmentList();
        ObservableList<Appointment> appointmentMonthList = FXCollections.observableArrayList(mainDatabase.appointmentList);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String currentDate = dateFormat.format(cal.getTime());

        cal.add(Calendar.MONTH, 1);
        String futureDate = dateFormat.format(cal.getTime());


        for (int i=0; i<appointmentMonthList.size(); i++){
            //System.out.println(appointmentWeekList.get(i).getStart_time());
            if ((appointmentMonthList.get(i).getStart_time().compareTo(currentDate) < 0) || (appointmentMonthList.get(i).getStart_time().compareTo(futureDate) > 0))
            {
                int id = appointmentMonthList.get(i).getAppointmentId();
                for(int x =0; x<mainDatabase.appointmentList.size(); x++){
                    if (mainDatabase.appointmentList.get(x).getAppointmentId() == id){
                        //System.out.println(mainDatabase.appointmentList.get(x).getStart_time());
                        mainDatabase.appointmentList.remove(x);
                        break;
                    }
                }
            }
        }

        SortedList<Appointment> sortedAppointments = new SortedList<>(mainDatabase.appointmentList);
        populateAppointmentTable();
    }

    @FXML
    void handleMainMenuButton(ActionEvent event) throws IOException {
        Parent mainMenuScreen = FXMLLoader.load(getClass().getResource("/sample/views/main_menu_screen.fxml"));
        Scene scene = new Scene(mainMenuScreen);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }



}
