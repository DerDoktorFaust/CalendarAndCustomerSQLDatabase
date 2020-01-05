package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import sample.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class mainDatabase {

    public static ObservableList<Customer> customerList = FXCollections.observableArrayList();
    public static ObservableList<City> cityList = FXCollections.observableArrayList();
    public static ObservableList<Country> countryList = FXCollections.observableArrayList();
    public static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    private static final String dbname = "U06r8z";
    private static final String url = "jdbc:mysql://3.227.166.251/" + dbname;
    private static final String username = "U06r8z";
    private static final String password = "53688848957";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static Connection conn;
    private static String current_user = "";
    private static int current_user_id = -1;

    public static void makeConnection()
    {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully to main database!");
        } catch(ClassNotFoundException cnfe){
            System.out.println("Can't find right class, ensure MySQL connector is added to project!");
        } catch(Exception excep){
            excep.printStackTrace();
        }
        populateStaticData();
    }

    public static Connection getCurrentConnection(){

        return conn;
    }

    public static void closeConnection()
    {
        try{
            conn.close();
        } catch (Exception excep){
            excep.printStackTrace();
        }
        System.out.println("Connection to main database is closed!");
    }

    public static String getCurrent_user() {
        return current_user;
    }

    public static void setCurrent_user(String current_user) {
        mainDatabase.current_user = current_user;
    }

    public static int getCurrent_user_id(){return current_user_id;}

    public static void setCurrent_user_id(int current_id){mainDatabase.current_user_id = current_id;}

    public static void populateStaticData(){
        // This method populates the list of cities and countries, which do not change because
        // the business only deals with those cities and countries

        int currentCityId;
        String currentCityName;
        int currentCountryId;
        String currentCountryName;

        // Populate city data
        try(PreparedStatement statement = mainDatabase.getCurrentConnection().prepareStatement(
                "SELECT city.cityId, city.city, city.countryId " +
                        "FROM city ");) {
            ResultSet results = statement.executeQuery();


            while (results.next()) {
                currentCityId = results.getInt("city.cityId");
                currentCityName = results.getString("city.city");
                currentCountryId = results.getInt("city.countryId");

                cityList.add(new City(currentCityId, currentCityName, currentCountryId));
            }
        }catch (SQLException sqe) {
            System.out.println("Error in SQL statement or connection");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }

        //Populate Country Data
        try(PreparedStatement statement = mainDatabase.getCurrentConnection().prepareStatement(
                "SELECT country.countryId, country.country " +
                        "FROM country ");) {
            ResultSet results = statement.executeQuery();


            while (results.next()) {
                currentCountryId = results.getInt("country.countryId");
                currentCountryName = results.getString("country.country");

                countryList.add(new Country(currentCountryId, currentCountryName));
            }
        }catch (SQLException sqe) {
            System.out.println("Error in SQL statement or connection");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }

    }

    public static List<Customer> getCustomerList()
    {
        customerList.clear(); // clear every time or this will repopulate with same data multiple times

        String currentCustomerId;
        String currentCustomerName;
        String currentAddress;
        String currentAddress2;
        String currentCity;
        String currentCountry;
        String currentPostalCode;
        String currentPhone;
        String currentlyActive;
        String currentAddressId;

        try(PreparedStatement statement = mainDatabase.getCurrentConnection().prepareStatement(
                "SELECT customer.customerId, customer.customerName, customer.active, customer.addressId, address.address, address.address2, address.postalCode, city.cityId, city.city, country.country, address.phone " +
                        "FROM customer, address, city, country " +
                        "WHERE customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId " +
                        "ORDER BY customer.customerId");) {
            ResultSet results = statement.executeQuery();


            while (results.next()) {
                currentCustomerId = results.getString("customer.customerId");
                currentCustomerName = results.getString("customer.customerName");
                currentAddress = results.getString("address.address");
                currentAddress2 = results.getString("address.address2");
                currentCity = results.getString("city.city");
                currentCountry = results.getString("country.country");
                currentPostalCode = results.getString("address.postalCode");
                currentPhone = results.getString("address.phone");
                currentlyActive = results.getString("customer.active");
                currentAddressId = results.getString("customer.addressId");

                if (!currentlyActive.equals("0")) {
                    customerList.add(new Customer(currentCustomerId, currentCustomerName, currentAddress, currentAddress2, currentCity, currentCountry, currentPostalCode, currentPhone, currentAddressId));
                }
            }
        }catch (SQLException sqe) {
            System.out.println("Error in SQL statement or connection");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
        return customerList;
    }

    public static void insertNewCustomer(String name, String address, String address2, int cityId, String zip, String phone) {
        try {
            int addressId = 0;
            //Insert into address table first
            PreparedStatement statement = mainDatabase.getCurrentConnection().prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES ('" + address + "','" + address2 + "'," + cityId + ",'" + zip + "','" + phone + "', CURRENT_TIMESTAMP, '" +  current_user + "', CURRENT_TIMESTAMP, '" + current_user + "');", Statement.RETURN_GENERATED_KEYS);
            statement.execute();

            //MySQL will auto-increment even based on deleted keys -- need to find the newly generated one!
            ResultSet keys = statement.getGeneratedKeys();
            if(keys.next()){
                addressId = keys.getInt(1);
            }

            //Insert into customer table
            String customerQuery = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES ('" + name + "'," + addressId + ",1, CURRENT_TIMESTAMP, '" + current_user + "', CURRENT_TIMESTAMP, '" + current_user + "');";
            statement.executeUpdate(customerQuery);
        } catch (SQLException e) {
            System.out.println("SQL Statement Error!" + e);
        }
    }

    public static void modifyCustomer(String name, String address, String address2, int cityId, String zip, String phone, int addressId, int customerId) {
        try {
            Statement statement = mainDatabase.getCurrentConnection().createStatement();
            String modifyCustomerQuery = "UPDATE customer SET customerName = '" + name + "', lastUpdateBy = '" + current_user + "' WHERE customerId =  " + customerId + ";";
            statement.execute(modifyCustomerQuery);

            String modifyAddressQuery = "UPDATE address SET address = '" + address + "', address2 = '" + address2 +
                    "', cityId = " + cityId + ", postalCode = '" + zip + "', phone = '" + phone + "', lastUpdateBy = '" + current_user + "' WHERE addressId = " + addressId;
            statement.execute(modifyAddressQuery);
        } catch (SQLException e){
            System.out.println("SQL Statement error! " + e);
        }
        getCustomerList();
    }

    public static void deleteCustomer(String customerId){
        try {
            Statement statement = mainDatabase.getCurrentConnection().createStatement();
            String deleteQuery = "UPDATE customer SET active = 0, lastUpdateBy = '" + current_user + "' WHERE customerId =  " + customerId + ";";
            statement.execute(deleteQuery);
        } catch (SQLException e){
            System.out.println("SQL Statement error! " + e);
        }
        getCustomerList();
    }



    public static List<Appointment> getAppointmentList() {
        appointmentList.clear(); // clear every time or this will repopulate with same data multiple times
        int currentappointmentId;
        int currentcustomerId;
        int currentuserId;
        String currenttitle;
        String currentdescription;
        String currentlocation;
        String currentcontact;
        String currenttype;
        String currenturl;
        String currentstart_time;
        String currentend_time;

        try{
            PreparedStatement statement = mainDatabase.getCurrentConnection().prepareStatement(
                    "SELECT * FROM appointment ORDER BY 'start' ");

            ResultSet appointmentResults = statement.executeQuery();


            while (appointmentResults.next()) {
                currentappointmentId = appointmentResults.getInt("appointment.appointmentId");
                currentcustomerId = appointmentResults.getInt("appointment.customerId");
                currentuserId = appointmentResults.getInt("appointment.customerId");
                currenttitle = appointmentResults.getString("appointment.title");
                currentdescription = appointmentResults.getString("appointment.description");
                currentlocation = appointmentResults.getString("appointment.location");
                currentcontact = appointmentResults.getString("appointment.contact");
                currenttype = appointmentResults.getString("appointment.type");
                currenturl = appointmentResults.getString("appointment.url");
                currentstart_time = appointmentResults.getString("appointment.start");
                currentend_time = appointmentResults.getString("appointment.end");

                currentstart_time = convertUTCToLocal(currentstart_time);
                currentend_time = convertUTCToLocal(currentend_time);

                appointmentList.add(new Appointment(currentappointmentId, currentcustomerId, currentuserId,
                        currenttitle, currentdescription, currentlocation, currentcontact, currenttype, currenturl, currentstart_time,
                        currentend_time));
            }

        }catch (SQLException e) {
            System.out.println("Error in SQL statement or connection");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }

        return appointmentList;
    }

    public static void checkForImminentAppointment(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String currentDate = dateFormat.format(cal.getTime());

        cal.add(Calendar.MINUTE, 15);
        String futureDate = dateFormat.format(cal.getTime());

        for (int i=0; i<appointmentList.size(); i++){
            if ((appointmentList.get(i).getStart_time().compareTo(currentDate) > 0) && (appointmentList.get(i).getStart_time().compareTo(futureDate) < 0))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initModality(Modality.NONE);
                alert.setTitle("Imminent Appointment");
                alert.setHeaderText("Imminent Appointment");
                alert.setContentText("Alert: You have an appointment in less than 15 minutes!");
                Optional<ButtonType> result = alert.showAndWait();
                break;
            }
        }
    }

    public static void insertNewAppointment(int customerId, int userId, String title,
                                            String description, String location, String contact, String type,
                                            String url, String start_time, String end_time){
        int new_appointment_id = -1;
        try {
            PreparedStatement statement = mainDatabase.getCurrentConnection().prepareStatement("INSERT INTO appointment " +
                "(customerId, userId, title, description, location, contact, type, url," +
                "start, end, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES " +
                "(" + customerId + ", " + userId + ",'" + title + "','" + description + "','" + location +
                "','" + contact + "', '" + type + "','" + url + "','" + start_time + "','" + end_time +
                "', CURRENT_TIMESTAMP, '" +  current_user + "', CURRENT_TIMESTAMP, '" + current_user + "');", Statement.RETURN_GENERATED_KEYS);

            //MySQL will auto-increment even based on deleted keys -- need to find the newly generated one!
            ResultSet keys = statement.getGeneratedKeys();
            if(keys.next()){
                new_appointment_id = keys.getInt(1);
            }
            statement.execute();
        }catch (SQLException e) {
            System.out.println("Error in SQL statement or connection");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
    }

    public static void modifyAppointment(int appointmentId, int customerId, int userId, String title,
                                         String description, String location, String contact, String type,
                                         String url, String start_time, String end_time) {
        try {
            Statement statement = mainDatabase.getCurrentConnection().createStatement();
            String modifyAppointmentQuery = "UPDATE appointment SET customerId = " + customerId + ", userId = " + userId +
                    ", title = '" + title + "', description = '" + description + "', location = '" + location +
                    "', contact = '" + contact + "', type = '" + type +
                    "', url = '" + url + "', start = '" + start_time + "', end = '" + end_time +
                    "', lastUpdateBy = '" + current_user + "', lastUpdate = CURRENT_TIMESTAMP " +
                    " WHERE appointmentId =  " + appointmentId + ";";
            statement.execute(modifyAppointmentQuery);

        } catch (SQLException e){
            System.out.println("SQL Statement error! " + e);
        }
        getCustomerList();
    }

    public static void deleteAppointment(int appointmentId){
        try {
            Statement statement = mainDatabase.getCurrentConnection().createStatement();
            String deleteQuery = "DELETE FROM appointment WHERE appointmentId = " + appointmentId + ";";
            statement.execute(deleteQuery);
        } catch (SQLException e){
            System.out.println("SQL Statement error! " + e);
        }
        getAppointmentList();
    }

    public static String convertUTCToLocal(String UTCDate){
        String newTime;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getDefault());
            Date date = dateFormat.parse(UTCDate);
            //System.out.println(dateFormat.format(date));

            String timeZone = Calendar.getInstance().getTimeZone().getID();
            Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
            newTime = dateFormat.format(local).toString();

            //System.out.println(dateFormat.format(local));
            return newTime;
        }catch(Exception e){
            System.out.println("Something went wrong!");
        }
        return "Error";
    }

    public static String convertLocalToUTC(String localTime){
        String newTime;
        try {
            DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateformat.setTimeZone(TimeZone.getDefault());
            Date localDate = dateformat.parse(localTime);
            dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
            newTime = dateformat.format(localDate);
            return newTime;

        }catch(Exception e){
            System.out.println("Something went wrong!" + e);
        }
        return "Error";
    }
}
