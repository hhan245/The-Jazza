package com.example.myshop.models;

import javafx.beans.property.*;

import java.text.MessageFormat;

public class Product {
    IntegerProperty ProductID;
    StringProperty ProductName;
    ObjectProperty<Category> category;
    StringProperty ImagePath;
    StringProperty Ingredient;
    LongProperty Price;

    public Product(){
        this.ProductID = new SimpleIntegerProperty();
        this.ProductName = new SimpleStringProperty();
        this.ImagePath = new SimpleStringProperty();
        this.Ingredient = new SimpleStringProperty();
        this.Price = new SimpleLongProperty();
        this.category = new SimpleObjectProperty<>();
    }
    public Product(int productID, String productName, Category category, String imagePath, String ingredient, long price){
        this.ProductID = new SimpleIntegerProperty(productID);
        this.ProductName = new SimpleStringProperty(productName);
        this.category = new SimpleObjectProperty<>(category);
        this.ImagePath = new SimpleStringProperty(imagePath);
        this.Ingredient = new SimpleStringProperty(ingredient);
        this.Price = new SimpleLongProperty(price);
    }
    public int getProductID() {
        return ProductID.get();
    }

    public Category getCategory() {
        return category.get();
    }

    public String getIngredient() {
        return Ingredient.get();
    }

    public long getPrice() {
        return Price.get();
    }

    public String getImagePath() {
        return ImagePath.get();
    }

    public String getProductName() {
        return ProductName.get();
    }
    public StringProperty NameProperty(){
        return ProductName;
    }

    public StringProperty ImagePathProperty(){
        return ImagePath;
    }
    public StringProperty IngredientProperty(){
        return Ingredient;
    }
    public LongProperty PriceProperty(){
        return Price;
    }
    public IntegerProperty ProductIDProperty(){return ProductID;}
    public ObjectProperty<Category> CategoryProperty(){return category;}

    public void setImagePath(String imagePath) {
        this.ImagePath.set(imagePath);
    }

    public void setProductName(String productName) {
        this.ProductName.set(productName);
    }

    public void setCategory(Category category) {
        this.category.set(category);
    }

    public void setIngredient(String ingredient) {
        this.Ingredient.set(ingredient);
    }

    public void setProductID(int productID) {
        this.ProductID.set(productID);
    }


    @Override
    public String toString(){
        int productID = getProductID();
        String productName = getProductName();
        String imagePath = getImagePath();
        String ingredient = getIngredient();
        long price = getPrice();
        return MessageFormat.format("{0} - {1} - {2} - {3} - {4}", productID, productName, imagePath, ingredient, price);
    }


    public Product clone(){
        return new Product(getProductID(), getProductName(), getCategory(), getImagePath(), getIngredient(), getPrice());
    }

    public void setPrice(long price) {
        this.Price.set(price);

    }}


