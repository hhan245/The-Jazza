package com.example.myshop.controllers;
import com.example.myshop.Main;
import com.example.myshop.models.*;
import com.example.myshop.models.Order;
import com.example.myshop.models.OrderDetail;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.text.Text;


import static javafx.scene.image.Image.*;



public class DashBoardController implements Initializable {
    @FXML
    private Label completed;

    @FXML
    private TableColumn<Order, String> customerNameCol;

    @FXML
    private TableView<Order> dashboard;

    @FXML
    private TableColumn<Order, LocalDateTime> dateTimeCol;

    @FXML
    private TableColumn<?, ?> detailColdb;

    @FXML
    private TableColumn<Order, String> idCol;

    @FXML
    private Label orderText;

    @FXML
    private Label revenue;

    @FXML
    private Label sold;

    @FXML
    private TableColumn<Order, String> statusCol;

    @FXML
    private TableColumn<Order, String> totalPriceCol;
    @FXML
    private Text drink1;

    @FXML
    private Text drink2;
    @FXML
    private AnchorPane line;

    @FXML
    private HBox line_shape1;

    @FXML
    private HBox line_shape2;

    @FXML
    private HBox line_shape3;

    @FXML
    private HBox line_shape4;

    @FXML
    private HBox line_shape5;

    @FXML
    private HBox line_shape6;

    @FXML
    private Text pizza1;

    @FXML
    private Text pizza2;

    @FXML
    private Text pizza3;

    @FXML
    private Label quantity1;

    @FXML
    private Label quantity2;

    @FXML
    private Label quantity3;
    @FXML
    private Label quantity4;

    @FXML
    private Label quantity5;

    @FXML
    private ImageView image1;

    @FXML
    private ImageView image2;

    @FXML
    private ImageView image3;
    @FXML
    private ImageView image4;

    @FXML
    private ImageView image5;

    @FXML
    private HBox line_shape;
    ObservableList<Order> orders = FXCollections.observableArrayList();
    ObservableList<Status> status1 = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Sold();
        Revenue();
        topDrink();
        topPizza();
        Completed();
        loadStatus();

        loadOrdersDashBoard();

        dashboard.setItems(orders);
        idCol.setCellValueFactory(param -> param.getValue().OrderIDProperty().asString());
        totalPriceCol.setCellValueFactory(param -> param.getValue().TotalPriceProperty().asString());
        dateTimeCol.setCellFactory(column -> {
            TableCell<Order, LocalDateTime> cell = new TableCell<Order, LocalDateTime>() {

                DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(format.format(getTableView().getItems().get(getIndex()).getDate()));
                    }
                }
            };

            return cell;
        });
        statusCol.setCellValueFactory(param -> {
            Order order = param.getValue();
            Status status = order.getStatus();
            if (status != null) {
                return new SimpleStringProperty(status.getStatusName());
            } else {
                return new SimpleStringProperty("N/A"); // Provide a default value or handle it as needed
            }
        });

    }

    public void Sold() {

        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("SELECT SUM(quantity) FROM orderdetails");
            if (resultSet.next()) {
                int soldQuantity = resultSet.getInt(1);
                sold.setText("" + soldQuantity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void Revenue() {
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("SELECT SUM(totalPrice) FROM orders WHERE statusID = 3");
            if (resultSet.next()) {
                int revenueQuantity = resultSet.getInt(1);
                revenue.setText("" + revenueQuantity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void Completed() {
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("SELECT COUNT(*) FROM orders WHERE statusID = 3");
            if (resultSet.next()) {
                int completedQuantity = resultSet.getInt(1);
                completed.setText("" + completedQuantity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void topPizza() {
        //top 3 pizza
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("SELECT products.productName, products.imagePath, SUM(orderdetails.quantity) AS quantity\n" +
                    "FROM orderdetails\n" +
                    "         JOIN products ON orderdetails.productID = products.productID\n" +
                    "         JOIN orders ON orderdetails.orderID = orders.orderID\n" +
                    "WHERE products.categoryID != 4 AND orders.statusID = 3\n" +
                    "GROUP BY products.productName, products.imagePath\n" +
                    "ORDER BY quantity DESC\n" +
                    "LIMIT 3;");
            if (resultSet.next()) {
                String pizzaName = resultSet.getString(1);
                String imagePath = resultSet.getString("imagePath");
                int quantity = resultSet.getInt(3);
                Image image = new Image("file:" + imagePath);
                String filePath = imagePath.replace("file:", "");
                String url = Main.class.getResource(filePath).toExternalForm();
                image1.setImage(new Image(url));
                pizza1.setText(pizzaName);
                quantity1.setText("Bánh Pizza đã bán: " + quantity);
            }
            if (resultSet.next()) {
                String pizzaName = resultSet.getString(1);
                String imagePath = resultSet.getString("imagePath");
                int quantity = resultSet.getInt(3);
                Image image = new Image("file:" + imagePath);
                String filePath = imagePath.replace("file:", "");
                String url = Main.class.getResource(filePath).toExternalForm();
                image2.setImage(new Image(url));
                pizza2.setText(pizzaName);
                quantity2.setText("Bánh Pizza đã bán: " + String.valueOf(quantity));
            }
            if (resultSet.next()) {
                String pizzaName = resultSet.getString(1);
                String imagePath = resultSet.getString("imagePath");
                int quantity = resultSet.getInt(3);
                Image image = new Image("file:" + imagePath);
                String filePath = imagePath.replace("file:", "");
                String url = Main.class.getResource(filePath).toExternalForm();
                image3.setImage(new Image(url));
                pizza3.setText(pizzaName);
                quantity3.setText("Bánh Pizza đã bán: " + String.valueOf(quantity));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void topDrink(){
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("SELECT products.productName, products.imagePath, SUM(orderdetails.quantity) AS quantity\n" +
                    "FROM orderdetails\n" +
                    "         JOIN products ON orderdetails.productID = products.productID\n" +
                    "WHERE products.categoryID = 4\n" +
                    "GROUP BY products.productName, products.imagePath\n" +
                    "ORDER BY quantity DESC\n" +
                    "LIMIT 3;");
            if (resultSet.next()) {
                String drinkName = resultSet.getString("productName");
                String imagePath = resultSet.getString("imagePath");
                int quantity = resultSet.getInt("quantity");
                Image image = new Image("file:" + imagePath);
                String filePath = imagePath.replace("file:", "");
                String url = Main.class.getResource(filePath).toExternalForm();
                image4.setImage(new Image(url));
                drink1.setText(drinkName);
                quantity4.setText("Nước đã bán: " + quantity);
            }
            if (resultSet.next()) {
                String drinkName = resultSet.getString("productName");
                String imagePath = resultSet.getString("imagePath");
                int quantity = resultSet.getInt("quantity");
                Image image = new Image("file:" + imagePath);
                String filePath = imagePath.replace("file:", "");
                String url = Main.class.getResource(filePath).toExternalForm();
                image5.setImage(new Image(url));
                drink2.setText(drinkName);
                quantity5.setText("Nước đã bán: " + quantity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    void loadOrdersDashBoard() {
        //load orders tu database
        orders.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from orders where isActive != false");
            while (resultSet.next()) {
                int orderID = resultSet.getInt("OrderID");
                System.out.println(orderID);
                LocalDateTime dateTime = resultSet.getTimestamp("DateTime").toLocalDateTime();
                System.out.println(dateTime);
                String customerName = resultSet.getString("CustomerName");
                String phoneNumber = resultSet.getString("PhoneNumber");
                System.out.println(customerName);
                long totalPrice = resultSet.getLong("TotalPrice");
                System.out.println(totalPrice);
                int statusID = resultSet.getInt("StatusID");
                System.out.println(statusID);

                ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
                ResultSet resultSet2 = mySQLConnector.queryResults("SELECT orderdetails.*, products.*, orders.customerName " +
                        "FROM orderdetails " +
                        "JOIN products ON orderdetails.ProductID = products.ProductID " +
                        "JOIN orders ON orderdetails.OrderID = orders.OrderID " +
                        "WHERE orderdetails.OrderID = " + orderID);
                Status status = null;
                while (resultSet2.next()) {
                    int productID = resultSet2.getInt("products.ProductID");
                    int quantity = resultSet2.getInt("Quantity");
                    String productName = resultSet2.getString("ProductName");
                    int categoryID = resultSet2.getInt("CategoryID");
                    String imagePath = resultSet2.getString("ImagePath");
                    String ingredient = resultSet2.getString("Ingredient");
                    long price = resultSet2.getLong("Price");
                    String customerName2 = resultSet2.getString("orders.customerName");


                    status = getStatusByID(statusID);
                    Product product = new Product(productID, productName, null, imagePath, ingredient, price);

                    orderDetails.add(new OrderDetail(orderID, product, quantity));
//
                }

                Order order = new Order(orderID, orderDetails, dateTime, totalPrice, customerName, phoneNumber, status);
                orders.add(order);
                System.out.println(MessageFormat.format("{0} - {1} - {2} - {3} - {4} - {5} - {6}", orderID, dateTime, totalPrice, orderDetails, totalPrice, customerName, statusID));
            }


        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private Status getStatusByID(int statusID) {
        System.out.println("Status:" + status1);
        for (Status status : status1) {
            System.out.println("statusID: " + status.getStatusID());
            System.out.println("statusInput: " + status.getStatusID());
            if (status.getStatusID() == statusID)
                return status;
        }
        return null;
    }

    void loadStatus() {
        status1.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from status");
            while (resultSet.next()) {
                int statusID = resultSet.getInt(1);
                String statusName = resultSet.getString(2);
                Status status = new Status(statusID, statusName);
                status1.add(status);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}    
