package com.example.myshop.controllers;

import com.example.myshop.models.Order;
import com.example.myshop.models.Product;
import com.example.myshop.models.Status;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeStatus implements Initializable {
    private Order selectedOrder;
    private OrdersTabController ordersTabController;

    @FXML
    private ComboBox<String> statusComboBox;  // Using String ComboBox

    @FXML
    private Button okBtn;
    @FXML
    private TableView<Order> tableView;

    private Order order;
    ObservableList<Order> orders = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeStatusComboBox();
        okBtn.setOnAction(event -> {
            String selectedStatus = statusComboBox.getValue();
            if (selectedStatus != null) {
                Status newStatus = mapStatus(selectedStatus);
                if (newStatus != null) {
                    MySQLConnector connector = MySQLConnector.getInstance();
                    String sql = "UPDATE orders SET StatusID = " + newStatus.getStatusID() + " WHERE OrderID = " + selectedOrder.getOrderID();
                    if (connector.queryUpdate(sql)) {
                        selectedOrder.setStatus(newStatus);

                        // Update the status directly in the OrdersTabController's orders list
                        ordersTabController.updateOrders(selectedOrder);

                        closeWindow();
                    }
                }
            }
        });
    }

    public void setOrder(Order order) {
        selectedOrder = order;
        this.order = order;
    }

    public void setOrdersTabController(OrdersTabController controller) {
        ordersTabController = controller;
    }

    public void initializeStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList("Đang xử lý", "Đã hủy", "Đã hoàn thành"));
        statusComboBox.setOnAction(event -> {
            String status = (String) statusComboBox.getValue();
            if (statusComboBox.getSelectionModel().getSelectedIndex() == 0) {
                order.setStatus(new Status(1, "Đang xử lý"));
            } else if (statusComboBox.getSelectionModel().getSelectedIndex() == 1) {
                order.setStatus(new Status(2, "Đã huỷ"));
            } else if (statusComboBox.getSelectionModel().getSelectedIndex() == 2) {
                order.setStatus(new Status(3, "Đã hoàn thành"));
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) okBtn.getScene().getWindow();
        stage.close();
    }

    private Status mapStatus(String statusName) {
        switch (statusName) {
            case "Đang xử lý":
                return new Status(1, "Đang xử lý");
            case "Đã hủy":
                return new Status(2, "Đã hủy");
            case "Đã hoàn thành":
                return new Status(3, "Đã hoàn thành");
            default:
                return null;
        }
    }
}
