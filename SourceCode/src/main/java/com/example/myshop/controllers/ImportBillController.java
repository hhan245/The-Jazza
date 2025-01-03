package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Order;
import com.example.myshop.models.OrderDetail;
import com.example.myshop.models.Product;
import com.example.myshop.utils.Storing;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

public class ImportBillController implements Initializable {
    @FXML
    private Button okBtn;
    @FXML
    private Label orderDateLabel;
    @FXML
    private Label discount;
    @FXML
    private Label realPrice;

    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label totalProduct;
    @FXML
    private Label customerName;
    @FXML
    private Label phoneNumber;
    @FXML
    private TableView<OrderDetail> orderDetailsTable;

    @FXML
    private TableColumn<OrderDetail, String> productNameCol;

    @FXML
    private TableColumn<OrderDetail, Integer> quantityCol;
    @FXML
    private TableColumn<OrderDetail, String> priceCol;
    @FXML
    private TableColumn<OrderDetail, String> totalPriceCol;
    @FXML
    private TableColumn<OrderDetail, String> idCol;
    @FXML
    private CreateOrderController createOrderController;
    ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
    Order order;
    @FXML
    private void OK(ActionEvent event) {
        // Đóng Stage (cửa sổ) của nút đóng
        Stage stage = (Stage) okBtn.getScene().getWindow();
        stage.close();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization code, if needed.
    }
    public void init(Order order) {
        // Update controls with order information
        orderDateLabel.setText(order.getDate().toString());
        //add currency in realPrice
        realPrice.setText(NumberFormat.getInstance().format(order.getTotalPrice()));
        // Set up the order details table
        productNameCol.setCellValueFactory(param -> param.getValue().getProduct().NameProperty());
        quantityCol.setCellValueFactory(param -> param.getValue().QuantityProperty().asObject());
        priceCol.setCellValueFactory(param -> param.getValue().getProduct().PriceProperty().asString());
        totalPriceCol.setCellValueFactory(param -> {
            long total = param.getValue().calculateTotalPrice();
            return new SimpleObjectProperty<>(NumberFormat.getInstance().format(total));
        });
        idCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(orderDetailsTable.getItems().indexOf(cellData.getValue()) + 1).asString());
        totalProduct.setText(String.valueOf(order.getTotalQuantity()));
        customerName.setText(order.getCustomerName());
        phoneNumber.setText(order.getPhoneNumber());
        orderDetailsTable.setItems(FXCollections.observableArrayList(order.getList()));
        if (order.getTotalPrice() >= 500000){
            discount.setText("30%");
            //add currency
            totalPriceLabel.setText(NumberFormat.getInstance().format(order.getTotalPrice() * 0.7));
        }
        else {
            discount.setText("0%");
        }
        //

    }
}
