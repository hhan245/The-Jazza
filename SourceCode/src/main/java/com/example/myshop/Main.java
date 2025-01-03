package com.example.myshop;

import com.example.myshop.utils.AESCryptoprocessor;
import com.example.myshop.utils.MySQLConnector;
import com.example.myshop.utils.Storing;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String storedUsername = Storing.getValueToPreferences("username");
        String storedPassword = Storing.getValueToPreferences("password");

        System.out.println(storedUsername + "-" + storedPassword);

        AESCryptoprocessor aesCryptoprocessor = new AESCryptoprocessor();
        storedPassword = aesCryptoprocessor.decrypt(storedPassword);
        MySQLConnector connector = MySQLConnector.getInstance();

        System.out.println(storedUsername + "-" + storedPassword);
        if (connector.Connect(storedUsername, storedPassword)){
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/main-window.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 750);
            stage.setResizable(false);

            stage.setTitle("MainWindow!");
            stage.setScene(scene);
            stage.show();
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/login-window.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
            stage.setResizable(false);
            stage.setTitle("Login!");


            stage.setScene(scene);
            stage.show();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}