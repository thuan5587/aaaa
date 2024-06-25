package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection databaseLink;

    public static Connection getConnection() {
        String databaseName = "pocbase";
        String databaseUser = "root";
        String databasePassword = "root_password";
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return databaseLink;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
