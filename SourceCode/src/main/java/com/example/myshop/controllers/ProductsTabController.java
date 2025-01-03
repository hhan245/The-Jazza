package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Category;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;



public class ProductsTabController implements Initializable {
    @FXML
    private Button addBtn;

    @FXML
    private TableColumn<Product, String> categoryCol;

    @FXML
    private TableColumn<Product, String> idCol;

    @FXML
    private TableColumn<Product, Image> imageCol;

    @FXML
    private TableColumn<Product, String> nameCol;

    @FXML
    private TableColumn<Product, String> priceCol;

    @FXML
    private TableColumn<Product, String> ingredientCol;
    @FXML
    private TableColumn<Product, Button> actionCol;
    @FXML
    private TableView<Product> tableView;
    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;
    @FXML
    private Label totalPageLabel;
    @FXML
    private Label currentPageLabel;
    @FXML
    private TextField searchField;

    private int currentPage = 1;
    private int totalPage = 1;
    private int pageSize = 9;

    //du lieu luu trong db
    ObservableList<Product> products = FXCollections.observableArrayList();
    ObservableList<Category> categories = FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int totalItems = countAllProducts();
        totalPage = (int)Math.ceil(1.0 * totalItems/pageSize);
        currentPage = 1;

        currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
        totalPageLabel.textProperty().bind(new SimpleIntegerProperty(totalPage).asString());
        prevBtn.setOnAction(event -> {
            if (currentPage > 1){
                currentPage--;
                currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
                loadProducts();
            }
        });
        nextBtn.setOnAction(event -> {
            if (currentPage < totalPage){
                currentPage++;
                currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
                loadProducts();
            }
        });

        tableView.setItems(products);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);
        });
        idCol.setCellValueFactory(param -> param.getValue().ProductIDProperty().asString());
        nameCol.setCellValueFactory(param -> param.getValue().NameProperty());
        ingredientCol.setCellValueFactory(param -> param.getValue().IngredientProperty());
        priceCol.setCellFactory(param -> {
            TableCell<Product, String> cell = new TableCell<Product, String>(){
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty){
                        int rowID = getIndex();
                        Product product = products.get(rowID);

                        Locale locale = new Locale("vn", "VN");
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                        setText(currencyFormatter.format(product.getPrice()));
                    }
                    else {
                        setText(null);
                    }

                }
            };
            return cell;
        });
        //quantityCol.setCellValueFactory(param -> param.getValue().QuantityProperty().asString());

        categoryCol.setCellValueFactory(param -> param.getValue().getCategory().CategoryNameProperty());

        //hinh anh

        imageCol.setCellFactory(param -> {
            final ImageView imageView = new ImageView();
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            TableCell<Product, Image> cell = new TableCell<Product, Image>(){
                @Override
                public void updateItem(Image item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty){
                        System.out.println(getTableView().getItems().get(getIndex()).getProductName());
                        String filePath = getTableView().getItems().get(getIndex()).getImagePath();
                        if (filePath.equals("")){
                            filePath = "image-pizza/3loaiphomainhalam.png";
                        }
                        String url = Main.class.getResource(filePath).toExternalForm();
                        imageView.setImage(new Image(url));
                        setGraphic(imageView);
                    }
                    else setGraphic(null);

                }
            };

            return cell;
        });










        //Xoa san pham
        actionCol.setCellFactory(param -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("components/delete-btn-component.fxml"));
                final Button btn = fxmlLoader.load();
                TableCell<Product, Button> cell = new TableCell<Product, Button>(){
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty){
                            btn.setOnAction(event -> {
                                int rowID = getIndex();
                                Product product = products.get(rowID);
                                System.out.println("Xoa san pham" + product.getProductName());

                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + product.getProductName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                                alert.showAndWait();

                                if (alert.getResult() == ButtonType.YES) {
                                    deleteProductByID(product.getProductID());
                                }
                            });

                            setGraphic(btn);
                            setAlignment(Pos.CENTER);
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        }
                        else {
                            setGraphic(null);
                        }

                    }
                };
                return cell;
            }
            catch (Exception ex){
                System.out.println(ex);
            }
            return null;
        });




        loadCategories();
        loadProducts();
        addBtn.setOnAction(event -> {
                    //Mo window nhap san pham Stage stage = new Stage;
                    try{
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/add-product-window.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 400, 600);
                        stage.setResizable(false);
                        AddProductController controller = fxmlLoader.getController();
                        controller.initParams(this, categories);
                        stage.setTitle("Thêm Sản Phẩm!");
                        stage.setScene(scene);
                        stage.show();
                    }catch (Exception ex){
                        System.out.println(ex);
                    }

                }
        );
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) //Checking double click
            {
                //dong` double click
                Product product = tableView.getSelectionModel().getSelectedItem();
                //Mo window nhap san stage = new Stage;
                try{
                    Stage stage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/edit-product-window.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 400, 600);
                    stage.setResizable(false);
                    EditProductController controller = fxmlLoader.getController();
                    controller.initParams(this, categories, product);
                    stage.setTitle("Sửa Sản Phẩm!");
                    stage.setScene(scene);
                    stage.show();

                }catch (Exception ex){
                    System.out.println(ex);
                }
            }
        });
    }
    void deleteProductByID(int productID){
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            String sql =  "UPDATE products \n" +
                    "SET isActive = false " +
                    "WHERE productID = " + productID;
            System.out.println(sql);
            mySQLConnector.queryUpdate(sql);
            loadProducts();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    Button deleteButton = new Button();
    //deleteButton.setOnAction(event -> )
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
    void loadProducts(){
        products.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select * from products where isActive = true limit " + pageSize + " offset " + (currentPage-1)*pageSize );
            while(resultSet.next()){
                int productID = resultSet.getInt(1);
                String productName = resultSet.getString(2);
                int categoryID = resultSet.getInt(3);
                String imagePath = resultSet.getString(4);
                String ingredient = resultSet.getString(5);
                long price = resultSet.getLong(6);

                Category category = getCategoryByID(categoryID);
                Product product = new Product(productID, productName,category, imagePath, ingredient, price);
                products.add(product);
            }
            nextBtn.setDisable(currentPage == totalPage);
            prevBtn.setDisable(currentPage == 1);
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private Category getCategoryByID(int categoryID){
        for(Category category: categories){
            if (category.getCategoryID() == categoryID){
                return category;
            }
        }
        return null;
    }
    public void addProduct(Product p){
        products.add(p);
    }
    int countAllProducts(){
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select count(*) from products");
            if(resultSet.next()){
                return resultSet.getInt(1);
            }

        }catch (Exception ex){
            System.out.println(ex);
        }
        return 0;
    }
    public void updateProduct(Product p){
        for(Product product: products){
            if (product.getProductID() == p.getProductID()){
                product.setProductName(p.getProductName());
                product.setCategory(p.getCategory());
                product.setImagePath(p.getImagePath());
                product.setIngredient(p.getIngredient());
                product.setPrice(p.getPrice());
                break;
            }
        }
    }
    private void filterProducts(String searchText) {
        products.clear();

        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            String searchQuery = "SELECT * FROM products WHERE isActive = true AND productName LIKE '%" + searchText + "%' LIMIT " + pageSize + " OFFSET " + (currentPage - 1) * pageSize;
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

            nextBtn.setDisable(currentPage == totalPage);
            prevBtn.setDisable(currentPage == 1);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
