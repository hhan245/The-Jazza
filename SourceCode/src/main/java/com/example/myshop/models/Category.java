package com.example.myshop.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.SimpleStyleableStringProperty;

public class Category {
    IntegerProperty CategoryID;
    StringProperty CategoryName;
    public Category(int CategoryID, String CategoryName){
        this.CategoryID = new SimpleIntegerProperty(CategoryID);
        this.CategoryName = new SimpleStringProperty(CategoryName);
    }

    public int getCategoryID() {
        return CategoryID.get();
    }

    public String getCategoryName() {
        return CategoryName.get();
    }
    public StringProperty CategoryNameProperty(){
        return  CategoryName;
    }
    public IntegerProperty CategoryIDProperty(){
        return  CategoryID;
    }
    @Override
    public String toString(){
        return getCategoryName();
    }
}
