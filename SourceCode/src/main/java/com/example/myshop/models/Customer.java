package com.example.myshop.models;

import javafx.beans.property.*;

import java.text.MessageFormat;

public class Customer {
    IntegerProperty totalPoint;
    StringProperty phoneNumber;


    public Customer() {
        this.totalPoint = new SimpleIntegerProperty();
        this.phoneNumber = new SimpleStringProperty();
    }

    public Customer(String phoneNumber, int totalPoint) {
        this.totalPoint = new SimpleIntegerProperty(totalPoint);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);

    }

    public int getTotalPoint() {
        return totalPoint.get();
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public IntegerProperty TotalPointProperty() {
        return totalPoint;
    }

    public StringProperty PhoneNumberProperty() {
        return phoneNumber;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint.set(totalPoint);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }


    @Override
    public String toString() {
        int totalPoint = getTotalPoint();
        String phoneNumber = getPhoneNumber();
        return MessageFormat.format("{0} - {1}", totalPoint, phoneNumber);
    }


    public Customer clone() {
        return new Customer(getPhoneNumber(), getTotalPoint());
    }
}



