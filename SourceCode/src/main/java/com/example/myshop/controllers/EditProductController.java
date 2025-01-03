package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Category;
import com.example.myshop.models.Order;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

public class EditProductController implements Initializable {
    Product product;
    ObservableList<Category> categories;
    @FXML
    private Button editBtn;

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
        editBtn.setOnAction(event -> {
            //update san pham trong Database
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "UPDATE products\n" +
                    "SET ProductID = " + product.getProductID() + ", ProductName = '" + product.getProductName() + "', CategoryID = " + product.getCategory().getCategoryID() + ", ImagePath = '" + product.getImagePath() + "', Ingredient = '" + product.getIngredient() + "', Price = " + product.getPrice() + "\n" +
                    "WHERE productID = " + product.getProductID();

            if (connector.queryUpdate(sql)) {
                productsTabController.loadProducts();
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }

        });
        selectImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter imageFilter
                    = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
            fileChooser.getExtensionFilters().add(imageFilter);
            fileChooser.setTitle("Select a File");

            // Show the file dialog to pick a file
            File selectedFile = fileChooser.showOpenDialog((Stage) ((Node) event.getSource()).getScene().getWindow());
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
                    imageView.setImage(new Image(destinationPath.toString()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initParams(ProductsTabController productsTabController, ObservableList<Category> categories, Product oldProduct) {
        product = oldProduct.clone();
        nameInput.textProperty().bindBidirectional(product.NameProperty());
        priceInput.textProperty().bindBidirectional(product.PriceProperty(), NumberFormat.getNumberInstance());
        ingredientInput.textProperty().bindBidirectional(product.IngredientProperty());

        //quantityInput.textProperty().bindBidirectional(product.QuantityProperty(), NumberFormat.getNumberInstance());

        this.productsTabController = productsTabController;
        this.categories = categories;
        categoryComboBox.setItems(categories);
        categoryComboBox.valueProperty().bindBidirectional(product.CategoryProperty());


        //update hinh anh tren form
        if (!product.getImagePath().equals("")) {
            String filePath = Main.class.getResource(product.getImagePath()).toExternalForm();
            System.out.print(filePath);
            imageView.setImage(new Image(filePath));
        }


    }
}
