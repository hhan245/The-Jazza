
package com.example.myshop.models;

import javafx.beans.property.*;

public class OrderDetail {
    private ObjectProperty<Product> product;
    private ObjectProperty<Order> order;
    private IntegerProperty quantity;
    private IntegerProperty orderDetailID;
    private LongProperty totalPrice;

    public OrderDetail(){
        product = new SimpleObjectProperty<>();
        quantity = new SimpleIntegerProperty();
        orderDetailID = new SimpleIntegerProperty();
    }
    public OrderDetail( int orderDetailID, Product product, int quantity){
        this.product = new SimpleObjectProperty<>(product);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.orderDetailID = new SimpleIntegerProperty(orderDetailID);
    }
    public OrderDetail(Product product, int quantity){
        this.product = new SimpleObjectProperty<>(product);
        this.quantity = new SimpleIntegerProperty(quantity);
    }
    public int getQuantity() {
        return quantity.get();
    }

    public Product getProduct() {
        return product.get();
    }
    public Order getOrder(){
        return order.get();
    }

    public int getOrderDetailID() {
        return orderDetailID.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID.set(orderDetailID);
    }

    public void setProduct(Product product) {
        this.product.set(product);
    }
    public ObjectProperty<Product> ProductProperty(){
        return product;
    }
    public IntegerProperty QuantityProperty(){
        return quantity;
    }
    public IntegerProperty IntegerProperty(){
        return quantity;
    }
    @Override
    public String toString() {
        //iphone 15 x 2
        return getProduct().getProductName() + " x " + getQuantity();
    }
    public void increaseQuantity(){
        setQuantity(getQuantity() + 1);
    }
    public void decreaseQuantity(){
        if (getQuantity() > 0)
            setQuantity(getQuantity() - 1);
    }
    public long calculateTotalPrice() {
        return getProduct().getPrice() * getQuantity();
    }




}
