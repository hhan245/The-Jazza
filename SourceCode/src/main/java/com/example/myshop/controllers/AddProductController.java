package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Category;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;


public class AddProductController implements Initializable {
    Product product;
    ObservableList<Category> categories;
    @FXML
    private Button addBtn;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField priceInput;

    @FXML
    private TextField ingredientInput;

    @FXML
    private Button selectImageButton;

    //luu context cua man hinh danh sach san pham de update
    private ProductsTabController productsTabController;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBtn.setOnAction(event -> {
            if (product.getImagePath() == null || product.getImagePath().isEmpty()) {
                // Hiển thị cảnh báo nếu không có hình ảnh được chọn
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn một hình ảnh trước khi thêm sản phẩm.");
                alert.showAndWait();
            } else {
                // Thêm vào Database nếu có hình ảnh được chọn
                MySQLConnector connector = MySQLConnector.getInstance();
                String sql = "INSERT INTO Products(ProductName, CategoryID, ImagePath, Ingredient, Price, isActive)\n" +
                        "VALUES ('" + product.getProductName() + "', " + product.getCategory().getCategoryID() + ", '" + product.getImagePath() + "', '" + product.getIngredient() + "', '" + product.getPrice() + "', true);";
                System.out.println(sql);
                if (connector.queryUpdate(sql)) {
                    productsTabController.loadProducts();
                    ((Node)(event.getSource())).getScene().getWindow().hide();
                }
            }
        });
        selectImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter imageFilter
                    = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
            fileChooser.getExtensionFilters().add(imageFilter);
            fileChooser.setTitle("Select a File");

            // Show the file dialog to pick a file
            File selectedFile = fileChooser.showOpenDialog( (Stage)((Node)event.getSource()).getScene().getWindow());
            if (selectedFile != null) {
                String folder = Main.class.getResource("image-pizza/").getPath();
                folder = folder.substring(1);
                try {
                    String fileName = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());
                    fileName = fileName + selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                    Path destinationPath = Paths.get(folder, fileName);

                    // Copy the selected file to the destination path
                    Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File saved to: " + destinationPath.toString());
                    product.setImagePath("image-pizza/" + fileName);
                    imageView.setImage(new Image( destinationPath.toString()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initParams(ProductsTabController productsTabController, ObservableList<Category> categories){
        product = new Product();
        nameInput.textProperty().bindBidirectional(product.NameProperty());
        priceInput.textProperty().bindBidirectional(product.PriceProperty(), NumberFormat.getNumberInstance());
        ingredientInput.textProperty().bindBidirectional(product.IngredientProperty());
        //ingredientInput.textProperty().bindBidirectional(product.IngredientProperty(), NumberFormat.getNumberInstance());

        this.productsTabController = productsTabController;
        this.categories = categories;
        categoryComboBox.setItems(categories);
        categoryComboBox.valueProperty().bindBidirectional(product.CategoryProperty());
    }

}
