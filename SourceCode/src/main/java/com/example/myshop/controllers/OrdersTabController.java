package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.*;
import com.example.myshop.models.Status;
import com.example.myshop.utils.MySQLConnector;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrdersTabController implements Initializable {

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField customerNameInput;
    @FXML
    private TableColumn<Order, Button> actionCol;

    @FXML
    private Button addBtn;
    @FXML
    private ComboBox<String> comboBoxOrder;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private HBox datePickerHBox;
    @FXML
    private Button okBtn;

    @FXML
    private TableColumn<Order, LocalDateTime> dateTimeCol;

    @FXML
    private TableColumn<Order, String> idCol;

    @FXML
    private TableColumn<Order, String> productsCol;
    @FXML
    private TableColumn<Order, String> customerNameCol;
    @FXML
    private TableColumn<Order, String> phoneNumberCol;

    @FXML
    private TableColumn<Product, String> ingredientCol;
    @FXML
    private TableColumn<Product, String> categoryCol;
    @FXML
    private TableColumn<Order, String> statusCol;
    @FXML
    private TableColumn<Order, String> totalPriceCol;


    @FXML
    private TableView<Order> tableView;


    ObservableList<Order> orders = FXCollections.observableArrayList();
    ObservableList<Category> categories = FXCollections.observableArrayList();
    ObservableList<Status> status1 = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int totalPage = 1;
    private int pageSize1 = 14;
    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;
    @FXML
    private Label totalPageLabel;
    @FXML
    private Label currentPageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(LocalDate.now());
        datePickerHBox.setVisible(false);
        comboBoxOrder.setItems(FXCollections.observableArrayList("Tất cả", "Tùy chọn"));
        comboBoxOrder.setOnAction(event -> {
            if (comboBoxOrder.getSelectionModel().getSelectedIndex() == 0) {
                loadOrders();
                datePickerHBox.setVisible(false);
            }else {
                datePickerHBox.setVisible(true);
                loadOrdersFromTimeToTime(fromDatePicker.getValue(), toDatePicker.getValue());

            }
        });
        okBtn.setOnAction(event -> {
            loadOrdersFromTimeToTime(fromDatePicker.getValue(), toDatePicker.getValue());

            // Run UI updates on the JavaFX Application Thread
            //Platform.runLater(() -> {
            // loadLineChartFromTimeToTime(fromDatePicker.getValue(), toDatePicker.getValue(),"Tùy chọn");
            //});


        });

        comboBoxOrder.getSelectionModel().select(0);
        loadOrders();

        int totalItems = countAllOrders();
        totalPage = (int) Math.ceil(1.0 * totalItems / pageSize1);
        currentPage = 1;
        currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
        totalPageLabel.textProperty().bind(new SimpleIntegerProperty(totalPage).asString());
        prevBtn.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
                loadOrders();
            }
        });
        nextBtn.setOnAction(event -> {
            if (currentPage < totalPage) {
                currentPage++;
                currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
                loadOrders();
            }
        });
        tableView.setItems(orders);
        loadCategories();
        loadStatus();
        customerNameCol.setCellValueFactory(cellData -> cellData.getValue().CustomerNameProperty());

        addBtn.setOnAction(event -> {
            //Mo window nhap san phamStage stage = new Stage;
            try {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/create-order-window.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                CreateOrderController controller = fxmlLoader.getController();
                controller.init(this);
                stage.setTitle("Tạo đơn hàng");
                stage.setScene(scene);
                stage.show();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });

        //Load data from customerNameInput to TableView
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        idCol.setCellValueFactory(param -> param.getValue().OrderIDProperty().asString());
        customerNameCol.setCellValueFactory(param -> param.getValue().CustomerNameProperty());
        phoneNumberCol.setCellValueFactory(param -> param.getValue().PhoneNumberProperty());
        statusCol.setCellValueFactory(param -> {
            Order order = param.getValue();
            Status status = order.getStatus();
            if (status != null) {
                return new SimpleStringProperty(status.getStatusName());
            } else {
                return new SimpleStringProperty("N/A"); // Provide a default value or handle it as needed
            }
        });
        /*
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Lấy đối tượng đơn hàng được chọn từ TableView
                Order selectedOrder = tableView.getSelectionModel().getSelectedItem();

                // Hiển thị ComboBox để chọn trạng thái mới
                ComboBox<String> statusComboBox = new ComboBox<>();
                statusComboBox.getItems().addAll("Đang xử lý", "Đã hoàn thành", "Đã hủy"); // Thêm các trạng thái vào ComboBox

                // Tạo một dialog hoặc cửa sổ pop-up để chứa ComboBox
                Dialog<String> dialog = new Dialog<>();
                dialog.getDialogPane().setContent(statusComboBox);
                // Tạo một nút "Lưu" để lưu trạng thái mới
                ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                // Xử lý sự kiện khi người dùng nhấn nút "Lưu"
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == saveButtonType) {
                        return statusComboBox.getValue();
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(newStatus -> {
                    // Cập nhật trạng thái đơn hàng trong cơ sở dữ liệu
                    selectedOrder.setStatus(newStatus);

                    // Lưu trạng thái mới vào cơ sở dữ liệu (sử dụng JDBC, Hibernate hoặc công nghệ lưu trữ dữ liệu của bạn)
                    ChangeStatus.updateStatus(selectedOrder);

                    // Cập nhật lại TableView để hiển thị trạng thái mới
                    tableView.refresh();
                });
            }
        });
        */



        totalPriceCol.setCellFactory(column -> {
            TableCell<Order, String> cell = new TableCell<>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        long amount = getTableView().getItems().get(getIndex()).getTotalPrice();
                        Locale locale = new Locale("vn", "VN");
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                        setText(currencyFormatter.format(amount));
                    }
                }
            };

            return cell;
        });

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
        productsCol.setCellFactory(column -> {
            TableCell<Order, String> cell = new TableCell<Order, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(getTableView().getItems().get(getIndex()).ProductsDisplay());
                    }
                }
            };

            return cell;
        });
        loadOrders();
        actionCol.setCellFactory(param -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("components/delete-btn-orders-component.fxml"));
                final Button btn = fxmlLoader.load();
                TableCell<Order, Button> cell = new TableCell<Order, Button>() {
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            btn.setOnAction(event -> {
                                int rowID = getIndex();
                                Order order = orders.get(rowID);

                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete Order " + order.getOrderID() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                                alert.showAndWait();

                                if (alert.getResult() == ButtonType.YES) {
                                    deleteOrderByID(order.getOrderID());
                                    //  tu viet ham delete
                                }
                            });


                            setGraphic(btn);
                        } else {
                            setGraphic(null);
                        }

                    }
                };
                return cell;
            } catch (Exception ex) {
                System.out.println(ex);
            }
            return null;
        });
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Single-click
                Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
                if (selectedOrder != null) {
                    openChangeStatusWindow(selectedOrder);
                }
            }
        });

    }

    void deleteOrderByID(int orderID) {
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            String deleteOrderDetailsSQL = "DELETE FROM orderdetails WHERE OrderID = " + orderID;
            mySQLConnector.queryUpdate(deleteOrderDetailsSQL);

            // Sau đó xóa đơn hàng từ bảng orders
            String deleteOrderSQL = "DELETE FROM orders WHERE OrderID = " + orderID;
            mySQLConnector.queryUpdate(deleteOrderSQL);
            System.out.println("Delete order " + orderID + " successfully");
            mySQLConnector.queryUpdate(deleteOrderSQL);
            loadOrders();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    int countAllOrders() {
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select count(*) from orders");
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return 0;
    }

    void loadOrders(){
        //load orders tu database
        orders.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select * from orders where isActive != false LIMIT " + pageSize1 + " OFFSET " + (currentPage - 1) * pageSize1);
            while(resultSet.next()) {
                int orderID = resultSet.getInt("OrderID");
                System.out.println(orderID);
                LocalDateTime dateTime = resultSet.getTimestamp("DateTime").toLocalDateTime();
                System.out.println(dateTime);
                String customerName = resultSet.getString("CustomerName");
                System.out.println(customerName);
                String phoneNumber = resultSet.getString("PhoneNumber");
                long totalPrice = resultSet.getLong("TotalPrice");
                System.out.println(totalPrice);
                int statusID = resultSet.getInt("StatusID");
                System.out.println(statusID);

                ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
                ResultSet resultSet2 = mySQLConnector.queryResults("SELECT orderdetails.*, products.*, orders.customerName, orders.phoneNumber " +
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
            nextBtn.setDisable(currentPage == totalPage);
            prevBtn.setDisable(currentPage == 1);

        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private void loadOrdersFromTimeToTime(LocalDate from, LocalDate to) {
        try {
            // Execute the query and update the orders list
            MySQLConnector mySQLConnector = MySQLConnector.getInstance();
            String query = "SELECT * FROM orders WHERE orders.DateTime >= '" + from.atStartOfDay() + "' AND orders.DateTime <=  '" + to.atTime(23, 59, 59) + "'" ;

            ResultSet resultSet = mySQLConnector.queryResults(query);

            // Process the result set and update the orders list
            orders.clear();
            while (resultSet.next()) {
                int orderID = resultSet.getInt("OrderID");
                LocalDateTime dateTime = resultSet.getTimestamp("DateTime").toLocalDateTime();
                String customerName = resultSet.getString("CustomerName");
                String phoneNumber = resultSet.getString("PhoneNumber");
                long totalPrice = resultSet.getLong("TotalPrice");
                int statusID = resultSet.getInt("StatusID");

                // Similar to the existing loadOrders method, construct orderDetails and create an Order object
                ObservableList<OrderDetail> orderDetails = constructOrderDetails(orderID);

                Status status = getStatusByID(statusID);
                Order order = new Order(orderID, orderDetails, dateTime, totalPrice, customerName, phoneNumber, status);

                orders.add(order);
            }

            // Refresh the TableView
            tableView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ObservableList<OrderDetail> constructOrderDetails(int orderID) {
        ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();

        try {
            ResultSet resultSet2 = mySQLConnector.queryResults("SELECT orderdetails.*, products.*, orders.customerName, orders.phoneNumber " +
                    "FROM orderdetails " +
                    "JOIN products ON orderdetails.ProductID = products.ProductID " +
                    "JOIN orders ON orderdetails.OrderID = orders.OrderID " +
                    "WHERE orderdetails.OrderID = " + orderID);

            while (resultSet2.next()) {
                // Construct OrderDetail objects similar to the existing loadOrders method
                int productID = resultSet2.getInt("products.ProductID");
                int quantity = resultSet2.getInt("Quantity");
                String productName = resultSet2.getString("ProductName");
                int categoryID = resultSet2.getInt("CategoryID");
                String imagePath = resultSet2.getString("ImagePath");
                String ingredient = resultSet2.getString("Ingredient");
                long price = resultSet2.getLong("Price");

                Product product = new Product(productID, productName, null, imagePath, ingredient, price);
                orderDetails.add(new OrderDetail(orderID, product, quantity));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return orderDetails;
    }


    public void updateOrders(Order o) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderID() == o.getOrderID()) {
                orders.set(i, o);
                tableView.refresh(); // Refresh the TableView to reflect the changes
                break;
            }
        }
    }

    void loadCategories() {
        categories.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from categories");
            while (resultSet.next()) {
                int categoryID = resultSet.getInt(1);
                String categoryName = resultSet.getString(2);
                Category category = new Category(categoryID, categoryName);
                categories.add(category);
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

    private void openChangeStatusWindow(Order selectedOrder) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myshop/windows/change-status.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 150);
            stage.setResizable(false);
            ChangeStatus changeStatusController = fxmlLoader.getController();

            changeStatusController.setOrdersTabController(this);  // Set the ordersTabController
            changeStatusController.setOrder(selectedOrder);

            stage.setTitle("Change Order Status");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initialize() {
        // Set the TableView to use your ObservableList
        tableView.setItems(orders);
    }
}