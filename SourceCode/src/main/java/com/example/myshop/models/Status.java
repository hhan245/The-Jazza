package com.example.myshop.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.SimpleStyleableStringProperty;

public class Status {
    IntegerProperty StatusID;
    StringProperty StatusName;
    public Status(int StatusID, String StatusName){
        this.StatusID = new SimpleIntegerProperty(StatusID);
        this.StatusName = new SimpleStringProperty(StatusName);
    }

    public int getStatusID() {
        return StatusID.get();
    }

    public String getStatusName() {
        return StatusName.get();
    }
    public StringProperty StatusNameProperty(){
        return  StatusName;
    }
    public IntegerProperty StatusIDProperty(){
        return  StatusID;
    }
    @Override
    public String toString(){
        return getStatusName();
    }
}