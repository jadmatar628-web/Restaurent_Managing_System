package com.jadmatar.restaurant.database;

import java.sql.*;

public class DatabaseConnectionCheck {

    public static void main(String[] args) {
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        try(Connection connection=DriverManager.getConnection(url,username,password)){
            System.out.println("Connection succeeded");
        }
        catch(SQLException exception){
            System.out.println("Database connection failed");
            System.out.println(exception.getMessage());
        }
    }}