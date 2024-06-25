package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Optional;

public class LoginController {
    @FXML
    private Button signup_btn;

    @FXML
    private TextField su_email;

    @FXML
    private TextField su_password;

    @FXML
    private TextField su_username;

    @FXML
    private Button cancelButton;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private String username;

    private Client client;

    private String publicKey;

    @FXML
    public void initialize() {

    }

    public void loginButtonOnAction(ActionEvent e) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 8088);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("login");
            String check = null;
            if ((check = in.readLine()) != null) {
                publicKey = check;
            }
            String userName = usernameTextField.getText();
            String pass = passwordPasswordField.getText();
            out.println(userName);
            out.println(PasswordEncryption.encryptData(pass, publicKey));
            String check1 = null;
            if ((check1 = in.readLine()) != null) {
                if (check1.equals("true")) {
                    this.username = userName; // Gán giá trị username sau khi đăng nhập thành công
                    openViewMenuWindow();
                } else if (check1.equals("false")) {
                    loginMessageLabel.setText("Sai tài khoản hoặc mật khẩu");
                }
            }
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }


    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    private void openViewMenuWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ViewMenu.fxml"));
            Parent root = fxmlLoader.load();

            Client clientController = fxmlLoader.getController();
            clientController.setUsername(username); // Gọi phương thức setUsername

            Stage stage = new Stage();
            stage.setTitle("Welcome");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void OnSever(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chat App");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CreateAccount(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAccount.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Create Account");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void signup() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 8088);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("publicKey");
            String check = null;
            if ((check = in.readLine()) != null) {
                publicKey = check;
            }

            String userName = su_username.getText();
            String gmail = su_email.getText();
            String pass = su_password.getText();

            // Kiểm tra email hợp lệ
            if (!gmail.endsWith("@gmail.com")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Email không hợp lệ!");
                alert.showAndWait();
                return; // Dừng quá trình đăng ký nếu email không hợp lệ
            }

            // Kiểm tra tên đăng nhập và mật khẩu không chứa ký tự đặc biệt
            if (userName.matches(".*[!@#$%^&*()<>?:\"{}+].*") || pass.matches(".*[!@#$%^&*()<>?:\"{}+].*")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Tên đăng nhập hoặc mật khẩu không hợp lệ! Xin vui lòng không sử dụng các ký tự đặc biệt.");
                alert.showAndWait();
                return; // Dừng quá trình đăng ký nếu tên đăng nhập hoặc mật khẩu không hợp lệ
            }

            // Kiểm tra các trường thông tin không được rỗng
            if (userName.isEmpty() || pass.isEmpty() || gmail.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng điền đầy đủ thông tin.");
                alert.showAndWait();
                return; // Dừng quá trình đăng ký nếu có trường thông tin rỗng
            }

            // Gửi thông tin đăng ký tới server
            out.println(userName);
            out.println(gmail);
            out.println(PasswordEncryption.encryptData(pass, publicKey));

            // Đọc phản hồi từ server (nếu cần)
            String response = in.readLine();
            if (response != null && response.equals("success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Tạo tài khoản thành công!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Tạo tài khoản thành công!");
                alert.showAndWait();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}