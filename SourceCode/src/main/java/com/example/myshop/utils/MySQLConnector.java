package com.example.myshop.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class MySQLConnector {
    String url = "jdbc:mysql://localhost:3306/myshopdb";
    Connection connection;
    public static MySQLConnector instance = null;
    private MySQLConnector(){

    }
    public static MySQLConnector getInstance(){
        if (instance ==null){
            instance = new MySQLConnector();
        }
        return instance;
    }
    public boolean Connect(String username, String password){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connect successful.........");
            return true;
        } catch (Exception  ex) {
            System.out.println(ex);
            System.out.println("Connect fail..........");
        }
        return false;
    }
    public boolean queryUpdate(String sql){
        if (connection != null){
            try{
                connection.createStatement().executeUpdate(sql);
                return true;
            }catch (Exception ex){
                System.out.println(ex);
                return false;
            }

        }
        return false;
    }
    public ResultSet queryResults(String sql){
        if (connection != null){
            try{
                return connection.createStatement().executeQuery(sql);
            }catch (Exception ex){
                System.out.println(ex);
            }

        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    public class database {

        public static Connection getConnection() {

            try {

                Class.forName("com.mysql.jdbc.Driver");

                Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "123456789"); // LINK YOUR DATABASE
                return connect;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
