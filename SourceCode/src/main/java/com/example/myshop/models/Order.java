package com.example.myshop.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    ObservableList<OrderDetail> list = FXCollections.observableArrayList();
    ;
    ObjectProperty<LocalDateTime> dateTime;
    LongProperty totalPrice;
    IntegerProperty orderID;
    StringProperty customerName;
    StringProperty phoneNumber;
    StringProperty StatusName;
    IntegerProperty StatusID;
    ObjectProperty<Status> status;

    public Order() {
        dateTime = new SimpleObjectProperty<>(LocalDateTime.now());
        totalPrice = new SimpleLongProperty();
        orderID = new SimpleIntegerProperty();
        this.status = new SimpleObjectProperty<>();
        this.customerName = new SimpleStringProperty();
        this.phoneNumber = new SimpleStringProperty();

    }

    public Order(int orderID, ObservableList<OrderDetail> list, LocalDateTime dateTime, long totalPrice, String customerName, String phoneNumber, Status status) {

        this.list.addAll(list);
        this.dateTime = new SimpleObjectProperty<>(dateTime);
        this.totalPrice = new SimpleLongProperty(totalPrice);
        this.orderID = new SimpleIntegerProperty(orderID);
        this.status = new SimpleObjectProperty<>(status);
        this.customerName = new SimpleStringProperty(customerName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }



    public ObservableList<OrderDetail> getList() {
        return list;
    }

    public LocalDateTime getDate() {
        return dateTime.get();
    }

    //discount for 30% if total price > 250000 VND else return the original price
    public long getDiscount() {
        if (getTotalPrice() > 500000) {
            return (long) (getTotalPrice() * 0.7);
        } else {
            return getTotalPrice();
        }
    }
    public long getTotalPrice() {
        return totalPrice.get();
    }

    public int getOrderID() {
        return orderID.get();
    }

    public Status getStatus() {
        return status.get();
    }
    public String getCustomerName() {
        return customerName.get();
    }
    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setDateTime(LocalDateTime date) {
        this.dateTime.set(date);
    }

    public void setList(ObservableList<OrderDetail> list) {
        this.list = list;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public void setOrderID(int orderID) {
        this.orderID.set(orderID);
    }

    public IntegerProperty OrderIDProperty() {
        return orderID;
    }

    public LongProperty TotalPriceProperty() {
        return totalPrice;
    }

    public ObjectProperty<LocalDateTime> DateProperty() {
        return dateTime;
    }

    public ObjectProperty<Status> StatusProperty() {
        return status;
    }

    public StringProperty CustomerNameProperty() {
        return customerName;
    }
    public StringProperty PhoneNumberProperty() {
        return phoneNumber;
    }


    public void UpdateOrderDetail(ObservableList<OrderDetail> newList) {
        // Clear existing list
        this.list.clear();

        // Add the new list
        this.list.addAll(newList);

        // Update the total price
        long newTotalPrice = 0;
        for (int i = 0; i < newList.size(); i++) {
            newTotalPrice += newList.get(i).getProduct().getPrice() * newList.get(i).getQuantity();
        }
        setTotalPrice(newTotalPrice);
    }
    // sum total price of each product in order



    public String ProductsDisplay() {
        String res = "";
        for (int i = 0; i < list.size(); i++) {
            res += list.get(i).toString() + ", ";
        }
        if (!res.equals("")) {
            res = res.substring(0, res.length() - 2);
        }
        return res;
    }
    public Order clone(){
        return new Order(getOrderID(), getList(), getDate(), getTotalPrice(), getCustomerName(), getPhoneNumber(), getStatus());
    }

    public void setStatus(String selectedItem) {
    }
    public int getTotalQuantity() {
        int totalQuantity = 0;
        for (OrderDetail orderDetail : list) {
            totalQuantity += orderDetail.getQuantity();
        }
        return totalQuantity;
    }









}
