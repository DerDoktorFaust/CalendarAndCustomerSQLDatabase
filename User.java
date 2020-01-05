package sample;


import java.io.FileWriter;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import sample.mainDatabase;

public class User {

    private String username;
    private String password;
    private String loginTime;
    private int userId;

    public User(){
        this.username = "";
        this.password = "";
        this.userId = -1;
        this.loginTime = "-1";
    }

    public boolean attemptLogin(){


        try {
            PreparedStatement statement = mainDatabase.getCurrentConnection().prepareStatement("SELECT * FROM user WHERE userName = '" + this.username +
                    "' and password ='" + this.password + "';");

            ResultSet loginResults = statement.executeQuery();

            if(loginResults.next()) {
                this.userId = loginResults.getInt("userId");
                this.loginTime = LocalDateTime.now().toString();
                mainDatabase.setCurrent_user(this.username);
                mainDatabase.setCurrent_user_id(this.userId);
                writeLogFile();
                return true;
            }

        } catch (SQLException e){
            System.out.println("SQL Statement error! " + e);
            return false;
        }
        return false;
    }

    public void writeLogFile(){
        try{
            FileWriter userlog = new FileWriter("userlog.txt", true);
            String data = "\n" + this.username + " " + this.loginTime;
            userlog.write(data);
            userlog.close();
        } catch (IOException e){
            System.out.println("Error creating log file: " + e);
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
