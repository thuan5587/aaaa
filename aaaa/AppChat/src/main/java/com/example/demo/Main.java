package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load giao diện từ tệp FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Hiển thị giao diện
        primaryStage.setTitle("Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
        // Khởi chạy server trong một luồng riêng biệt
        Thread serverThread = new Thread(() -> {
            try {
                Server server = new Server();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}