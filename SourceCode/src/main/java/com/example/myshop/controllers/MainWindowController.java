package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.utils.MySQLConnector;
import com.example.myshop.utils.Storing;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    @FXML
    HBox hBox;
    @FXML
    private ToggleButton dashBoardBtn;
    @FXML
    private ToggleButton productsBtn;
    @FXML
    private ToggleButton ordersBtn;
    @FXML
    private ToggleButton statsBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private ToggleButton eventBtn;

    @FXML
    private Label dashboardLabel;
    @FXML
    private ToggleButton customerBtn;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logoutBtn.setOnAction(event -> {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/login-window.fxml"));
            Scene scene = null;
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Error Message");
                alert.setHeaderText("null");
                alert.setContentText("Are you sure you want to log out?");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                scene = new Scene(fxmlLoader.load(), 1000, 700);
                    stage.setTitle("Login!");
                    stage.setScene(scene);
                    stage.show();
                    ((Node)(event.getSource())).getScene().getWindow().hide();
                    //xoa username + password
                    Storing.putValueToPreferences("username", "");
                    Storing.putValueToPreferences("password", "");

                }
                else if (option.get() == ButtonType.CANCEL) {
                    alert.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        dashBoardBtn.setOnAction(event -> {
            ChangeTab(1);
        });
        productsBtn.setOnAction(event -> {
            ChangeTab(2);
        });
        ordersBtn.setOnAction(event -> {
            ChangeTab(3);
        });
        statsBtn.setOnAction(event -> {
            ChangeTab(4);
        });
        eventBtn.setOnAction(event -> {
            ChangeTab(5);
        });
        customerBtn.setOnAction(event -> {
            ChangeTab(6);
        });



        ChangeTab(1);
    }
    void ChangeTab(int idTab){
        //idTab
        //1: dashboard,2: products, 3: orders, 4:stats

        //Remove thang thu 2 di
        if (hBox.getChildren().size() >= 2){
            hBox.getChildren().remove(1);
        }
        ChangeSelectedMenu(idTab);

        if (idTab == 1){
            Node node = loadTab("tabs/dashboard-tab.fxml");
            hBox.getChildren().add(node);
        }
        else if  (idTab == 2){
            Node node = loadTab("tabs/products-tab.fxml");
            hBox.getChildren().add(node);
        }
        else if  (idTab == 3){
            Node node = loadTab("tabs/orders-tab.fxml");
            hBox.getChildren().add(node);
        }
        else if  (idTab == 4){
            Node node = loadTab("tabs/stats-tab.fxml");
            hBox.getChildren().add(node);
        }
        else if  (idTab == 5){
            Node node = loadTab("tabs/event-tab.fxml");
            hBox.getChildren().add(node);
        }
        else if  (idTab == 6){
            Node node = loadTab("tabs/customer-tab.fxml");
            hBox.getChildren().add(node);
        }

    }
    Node loadTab(String fxmlPath){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Node node = fxmlLoader.load();
            System.out.println("load thanh cong");
            return node;
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }
    void ChangeSelectedMenu(int idTab){
        dashBoardBtn.setSelected(false);
        productsBtn.setSelected(false);
        ordersBtn.setSelected(false);
        statsBtn.setSelected(false);
        eventBtn.setSelected(false);
        customerBtn.setSelected(false);
        if (idTab == 1){
            dashBoardBtn.setSelected(true);
        }
        else if  (idTab == 2){
            productsBtn.setSelected(true);
        }
        else if  (idTab == 3){
            ordersBtn.setSelected(true);
        }
        else if  (idTab == 4){
            statsBtn.setSelected(true);
        }
        else if  (idTab == 5){
            eventBtn.setSelected(true);
        }
        else if  (idTab == 6){
            customerBtn.setSelected(true);
        }

    }


}
