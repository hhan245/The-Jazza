package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.utils.AESCryptoprocessor;
import com.example.myshop.utils.MySQLConnector;
import com.example.myshop.utils.Storing;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindowController implements Initializable {
    @FXML
    private Button loginBtn;
    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label errorText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        errorText.setText("");
        loginBtn.setOnAction(event -> {

            String username = usernameInput.getText();
            String password = passwordInput.getText();
            System.out.println(username + "-" + password);
            savePassword(username, password);
            MySQLConnector mySQLConnector = MySQLConnector.getInstance();
            //neu connect thanh cong
            if (mySQLConnector.Connect(username, password)){
                //CHuyen sang man hinh chinh
                try{
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setResizable(false);

                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/main-window.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 1200, 750);
                    stage.setTitle("MainWindow");
                    stage.setScene(scene);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
            else {
                errorText.setText("Sai tài khoản hoặc mật khẩu");
            }




        });
    }
    void savePassword(String username,String password){
        Storing.putValueToPreferences("username",username);
        AESCryptoprocessor aesCryptoprocessor = new AESCryptoprocessor();

        String encryptedPassword = aesCryptoprocessor.encrypt(password);
        Storing.putValueToPreferences("password",encryptedPassword);
    }
}
