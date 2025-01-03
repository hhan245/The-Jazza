package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Order;
import com.example.myshop.models.*;
import com.example.myshop.utils.MySQLConnector;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CreateOrderController implements Initializable {
    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button cancelBtn;

    @FXML
    private Label dateTime;
    @FXML
    private Button importBtn;

    @FXML
    private TableColumn<Product, String> idCol;

    @FXML
    private TableColumn<Product, Image> imageCol;

    @FXML
    private TableColumn<Product, String> nameCol;

    @FXML
    private TableColumn<Product, String> priceCol;

    @FXML
    private TableColumn<Product, String> categoryCol;
    @FXML
    private TableColumn<Product, String> ingredientCol;
    @FXML
    private TextField customerNameInput;

    @FXML
    private TextField phoneNumberInput;
    @FXML
    private TextField searchField;


    //@FXML
    //private TableColumn<Product, String> quantityCol;
    @FXML
    private TableColumn<Product, Button> actionCol;

    @FXML
    private ListView<OrderDetail> listView;

    @FXML
    private Button okBtn;


    @FXML
    private TableView<Product> tableView;

    @FXML
    private Label totalPrice;
    @FXML
    private Label realPrice;
    @FXML
    private Label discount;
    Order order;
    Customer customer;


    ObservableList<Product> products = FXCollections.observableArrayList();
    ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
    OrdersTabController ordersTabController;
    CreateOrderController createOrderController;
    ObservableList<Category> categories = FXCollections.observableArrayList();
    ObservableList<Status> status1 = FXCollections.observableArrayList();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        order = new Order();
        tableView.setItems(products);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);
        });
        idCol.setCellValueFactory(param -> param.getValue().ProductIDProperty().asString());
        categoryCol.setCellValueFactory(param -> param.getValue().CategoryProperty().asString());
        nameCol.setCellValueFactory(param -> param.getValue().NameProperty());
        ingredientCol.setCellValueFactory(param -> param.getValue().IngredientProperty());
        priceCol.setCellFactory(param -> {
            TableCell<Product, String> cell = new TableCell<Product, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        int rowID = getIndex();
                        Product product = products.get(rowID);

                        Locale locale = new Locale("vn", "VN");
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                        setText(currencyFormatter.format(product.getPrice()));
                    } else {
                        setText(null);
                    }

                }
            };
            return cell;
        });
        //quantityCol.setCellValueFactory(param -> param.getValue().QuantityProperty().asString());
        imageCol.setCellFactory(param -> {
            final ImageView imageView = new ImageView();
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            TableCell<Product, Image> cell = new TableCell<Product, Image>() {
                @Override
                public void updateItem(Image item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        System.out.println(getTableView().getItems().get(getIndex()).getProductName());
                        String filePath = getTableView().getItems().get(getIndex()).getImagePath();
                        if (filePath.equals("")) {
                            filePath = "imgs/default.png";
                        }
                        String url = Main.class.getResource(filePath).toExternalForm();
                        imageView.setImage(new Image(url));
                        setGraphic(imageView);
                    } else setGraphic(null);

                }
            };

            return cell;
        });

        dateTime.textProperty().bind(Bindings.createStringBinding(() -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
            return dtf.format(order.getDate());
        }, order.DateProperty()));

        //discount for 30% for the total price up to 250000 and add currency for locate Viet Nam
        totalPrice.textProperty().bind(Bindings.createStringBinding(() -> {
            Locale locale = new Locale("vn", "VN");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            if (order.getTotalPrice() >= 500000) {
                discount.setText("30%");
                return currencyFormatter.format(order.getTotalPrice() * 0.7);
            } else {
                discount.setText("0%");
                return currencyFormatter.format(order.getTotalPrice());
            }
        }, order.TotalPriceProperty()));



        realPrice.textProperty().bind(Bindings.createStringBinding(() -> {
            Locale locale = new Locale("vn", "VN");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            return currencyFormatter.format(order.getTotalPrice());
        }, order.TotalPriceProperty()));


        listView.setItems(orderDetails);
        listView.setCellFactory(param -> new ListViewItemController(orderDetails, order));

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) //Checking double click
            {
                Product product = tableView.getSelectionModel().getSelectedItem();

                OrderDetail orderDetail = findProductInListView(product, orderDetails);

                if (orderDetail == null)
                    orderDetails.add(new OrderDetail(product, 1));
                else {
                    orderDetail.increaseQuantity();
                }
                order.UpdateOrderDetail(orderDetails);
            }
        });
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
        customerNameInput.setOnKeyReleased(event -> {
            String customerName = customerNameInput.getText();
            order.setCustomerName(customerName);
        });

        phoneNumberInput.setOnKeyReleased(event -> {
            String phoneNumber = phoneNumberInput.getText();
            order.setPhoneNumber(phoneNumber);
        });
        cancelBtn.setOnAction(event -> {
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.close();
        });
        okBtn.setOnAction(event -> {
            //  Chua chon san pham nao
            if (orderDetails.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please choose at least 1 product");
                alert.showAndWait();
                return;
            }
            if (Objects.equals(order.getCustomerName(), "") || order.getCustomerName() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please enter customer name");
                alert.showAndWait();
                return;
            }
            if (order.getStatus() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please choose status");
                alert.showAndWait();
                return;
            }
            if (Objects.equals(order.getPhoneNumber(), "") || order.getPhoneNumber() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please enter phone number");
                alert.showAndWait();
                return;
            }
            //B1 tao don hang trong database
            int newOrderID = createOrderInDatabase();
            updateCustomerPoints();

            //B2 tao cac OrderDetails trong database
            createOrderDetailInDatabase(newOrderID);

            //load lai danh sach orders o Order Tab
            ordersTabController.loadOrders();
            Stage stage = (Stage) okBtn.getScene().getWindow();
            stage.close();
        });
        loadProducts();
    }

    OrderDetail findProductInListView(Product product, ObservableList<OrderDetail> list){
        for(int  i = 0; i < list.size(); i++)
        {
            if (product.getProductID() == list.get(i).getProduct().getProductID())
                return  list.get(i);
        }
        return null;
    }
    public void init(OrdersTabController ordersTabController){
        this.ordersTabController = ordersTabController;
    }



    void loadProducts(){

        products.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select * from products where isActive = true " );
            while(resultSet.next()){
                int productID = resultSet.getInt(1);
                String productName = resultSet.getString(2);
                int categoryID = resultSet.getInt(3);
                String imagePath = resultSet.getString(4);
                String ingredient = resultSet.getString(5);
                long price = resultSet.getLong(6);

                Category category = getCategoryByID(categoryID);
                Product product = new Product(productID, productName, category, imagePath, ingredient, price);
                products.add(product);
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    void loadCategories(){
        categories.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select * from categories");
            while(resultSet.next()){
                int categoryID = resultSet.getInt(1);
                String categoryName = resultSet.getString(2);
                Category category = new Category(categoryID, categoryName);
                categories.add(category);
            }

        }catch (Exception ex){
            System.out.println(ex);
        }
    }




    int createOrderInDatabase(){
        //them vao Database
        try{
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "INSERT INTO Orders(DateTime, TotalPrice, CustomerName, PhoneNumber, StatusID, isActive)\n" +
                    "VALUES ('" +order.getDate() + "', "+order.getDiscount() + ", '"+order.getCustomerName() +"','"+order.getPhoneNumber()+"', "+order.getStatus().getStatusID()+", true);";

            System.out.println(sql);
            if (connector.queryUpdate(sql)){
                sql = "SELECT LAST_INSERT_ID();";
                ResultSet resultSet = connector.queryResults(sql);
                if(resultSet.next()){
                    int newOrderID = resultSet.getInt(1);

                    // Calculate points
                    int points = (int) (order.getTotalPrice() / 1000);

                    // Update the Points column in the Orders table
                    sql = "UPDATE Orders SET Points = " + points + " WHERE OrderID = " + newOrderID + ";";
                    if (connector.queryUpdate(sql)) {
                        updateCustomerPoints();
                    }


                    return newOrderID;
                }
            }
        }catch (Exception ex){
            System.out.println(ex);
        }

        return -1;
    }
    //Insert into Customer (PhoneNumber) from Orders and calculate total points for each customer through certain PhoneNumber
    void updateCustomerPoints() {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "INSERT INTO Customers (PhoneNumber) SELECT PhoneNumber FROM Orders WHERE PhoneNumber NOT IN (SELECT PhoneNumber FROM Customers);";
            connector.queryUpdate(sql);

            // Update TotalPoints for all customers
            String updateAllCustomersQuery = "UPDATE Customers\n" +
                    "SET TotalPoints = (SELECT SUM(Points)\n" +
                    "                   FROM Orders\n" +
                    "                   WHERE Customers.PhoneNumber = Orders.PhoneNumber)";
            connector.queryUpdate(updateAllCustomersQuery);
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }



    void createOrderDetailInDatabase(int orderID){
        //them vao Database
        try{
            MySQLConnector connector = MySQLConnector.getInstance();
            for(int i = 0; i < orderDetails.size(); i++){
                OrderDetail orderDetail = orderDetails.get(i);
                String sql = "INSERT INTO OrderDetails(OrderID, ProductID, Quantity)\n" +
                        "VALUES (" +orderID + ", " + orderDetail.getProduct().getProductID() + ", "+ orderDetail.getQuantity() + ");";
                connector.queryUpdate(sql);

                }

        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private Category getCategoryByID(int categoryID){
        System.out.println("Categories:" + categories);
        for(Category category: categories){
            System.out.println("categoryID: " + category.getCategoryID());
            System.out.println("categoryInput: " + category.getCategoryID());
            if (category.getCategoryID() == categoryID)
                return category;
        }
        return null;
    }

    private Status getStatusByID(int statusID) {
        System.out.println("Status:" + status1);
        for (Status status : status1) {
            System.out.println("categoryID: " + status.getStatusID());
            System.out.println("categoryInput: " + status.getStatusID());
            if (status.getStatusID() == statusID)
                return status;
        }
        return null;
    }

    void loadStatus(){
        status1.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select * from status");
            while(resultSet.next()){
                int statusID = resultSet.getInt(1);
                String statusName = resultSet.getString(2);
                Status status = new Status(statusID, statusName);
                status1.add(status);
            }

        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private void filterProducts(String searchText) {
        products.clear();

        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            String searchQuery = "SELECT * FROM products WHERE isActive = true AND ProductName LIKE '%" + searchText + "%'";
            ResultSet resultSet = mySQLConnector.queryResults(searchQuery);

            while (resultSet.next()) {
                int productID = resultSet.getInt(1);
                String productName = resultSet.getString(2);
                int categoryID = resultSet.getInt(3);
                String imagePath = resultSet.getString(4);
                String ingredient = resultSet.getString(5);
                long price = resultSet.getLong(6);

                Category category = getCategoryByID(categoryID);
                Product product = new Product(productID, productName, category, imagePath, ingredient, price);
                products.add(product);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    @FXML
    private void handleImportBtn(ActionEvent event) {
        if (orderDetails.isEmpty()){
            showAlert("Error", "Please choose at least 1 product", "Please choose at least 1 product");
            return;
        }

        if (Objects.equals(order.getCustomerName(), "") || order.getCustomerName() == null){
            showAlert("Error", "Please enter customer name", "Please enter customer name");
            return;
        }

        if (order.getStatus() == null){
            showAlert("Error", "Please choose status", "Please choose status");
            return;
        }
        if (Objects.equals(order.getPhoneNumber(), "") || order.getPhoneNumber() == null){
            showAlert("Error", "Please enter phone number", "Please enter phone number");
            return;
        }

        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/import-bill-window.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 776, 620); // Thay thế width và height bằng kích thước mong muốn
            stage.setResizable(false);
            // Điều này giả định rằng ImportController có một phương thức initParams() để truyền dữ liệu cần thiết
            ImportBillController controller = fxmlLoader.getController();
            controller.init(order);
            stage.setTitle("Import Data");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }






}
