package com.example.myshop.controllers;

import com.example.myshop.models.Category;
import com.example.myshop.models.Customer;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CustomersTabController implements Initializable {

    @FXML
    private TableColumn<Customer, String> phoneNumberCol;
    @FXML
    private TableColumn<Customer, String> totalPointCol;

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<Customer> tableView;


    ObservableList<Customer> customers = FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCustomers();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCustomers(newValue);
        });
        tableView.setItems(customers);
        phoneNumberCol.setCellValueFactory(param -> param.getValue().PhoneNumberProperty());
        totalPointCol.setCellValueFactory(param -> param.getValue().TotalPointProperty().asString());

    }
    void loadCustomers(){
        customers.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select * from customers");
            while(resultSet.next()){
                int totalPoint = resultSet.getInt("TotalPoints");
                String phoneNumber = resultSet.getString("PhoneNumber");
                Customer customer = new Customer(phoneNumber, totalPoint);
                customers.add(customer);
                System.out.println(MessageFormat.format("{0} - {1}", totalPoint, phoneNumber));
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private void filterCustomers(String searchText) {
        customers.clear();

        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            String searchQuery = "SELECT * FROM customers WHERE PhoneNumber LIKE '%" + searchText + "%'";
            ResultSet resultSet = mySQLConnector.queryResults(searchQuery);

            while (resultSet.next()) {
                int totalPoint = resultSet.getInt("TotalPoints");
                String phoneNumber = resultSet.getString("PhoneNumber");
                Customer customer = new Customer(phoneNumber, totalPoint);
                customers.add(customer);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


}
