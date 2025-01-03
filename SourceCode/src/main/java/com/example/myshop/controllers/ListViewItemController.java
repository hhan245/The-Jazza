package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Order;
import com.example.myshop.models.OrderDetail;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.text.NumberFormat;

public class ListViewItemController extends ListCell<OrderDetail> {
    @FXML
    private Button decreaseBtn;

    @FXML
    private HBox hbox;

    @FXML
    private ImageView imageView;

    @FXML
    private Button increaseBtn;

    @FXML
    private Label name;

    @FXML
    private Label quantity;
    ObservableList<OrderDetail> list;
    Order order;

    public ListViewItemController(ObservableList<OrderDetail> list, Order order) {

        this.list = list;
        this.order = order;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("components/listview-item-component.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();

        } catch (Exception ex) {

        }
    }


    @Override
    protected void updateItem(OrderDetail item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            String filePath = item.getProduct().getImagePath();
            if (filePath.equals("")) {
                filePath = "imgs/default.png";
            }
            String url = Main.class.getResource(filePath).toExternalForm();
            imageView.setImage(new Image(url));

            name.textProperty().bindBidirectional(item.getProduct().NameProperty());
            quantity.textProperty().bindBidirectional(item.QuantityProperty(), NumberFormat.getNumberInstance());

            increaseBtn.setOnAction(event -> {
                item.increaseQuantity();
                order.UpdateOrderDetail(list);
            });

            decreaseBtn.setOnAction(event -> {
                item.decreaseQuantity();
                if (item.getQuantity() == 0) {
                    list.remove(item);
                }
                order.UpdateOrderDetail(list);
            });
            if (item.getQuantity() == 0) {
                list.remove(item);
            }
            //  item.getProduct().setQuantity(item.getProduct().getQuantity() + 1);
               order.UpdateOrderDetail(list);
            //  });

            //set graphic cho cell
            setGraphic(hbox);

        }

    }}
