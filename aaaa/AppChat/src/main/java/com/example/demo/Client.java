package com.example.demo;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    @FXML
    public TextArea lowerTextArea;
    @FXML
    public TextArea su_message;
    @FXML
    public TextArea upperTextArea;
    @FXML
    public Button send_btn;
    @FXML
    public Button logoutBtn;
    @FXML
    public Button minimizeBtn;
    @FXML
    public Button closeBtn;
    @FXML
    private Label space; // Label để hiển thị tên người dùng

    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;
    private String username; // Biến để lưu trữ tên người dùng

    private ConnectToServerService connectService;

    // Phương thức để cập nhật tên người dùng sau khi đăng nhập
    public void setUsername(String username) {
        this.username = username;
        space.setText(username);
    }

    public void setClientName(String name) {
        this.username = name;
    }

    public void initialize() {
        connectService = new ConnectToServerService();
        connectService.start();
    }

    public void close() {
        try {
            socket.close();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        String message = su_message.getText();
        sendMessageToServer(username + ": " + message); // Gửi tên người dùng cùng với tin nhắn
    }


    private void sendMessageToServer(String message) {
        try {
            su_message.clear(); // Clear the TextArea after sending the message
            out.writeBytes(message + "\n");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lớp Service để kết nối đến máy chủ
    private class ConnectToServerService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Thực hiện kết nối đến máy chủ
                        socket = new Socket("localhost", 8088);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new DataOutputStream(socket.getOutputStream());

                        // Lắng nghe dữ liệu từ máy chủ
                        while (true) {
                            String message = in.readLine();
                            if (message != null) {
                                // Hiển thị dữ liệu từ máy chủ lên TextArea
                                if (isMessageFromServer(message)) {
                                    lowerTextArea.appendText(message + "\n");
                                } else {
                                    upperTextArea.appendText(message + "\n");
                                }
                            } else {
                                // Đóng kết nối nếu máy chủ ngắt kết nối
                                closeConnection();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }

    // Phương thức kiểm tra xem tin nhắn có phải từ server không
    private boolean isMessageFromServer(String message) {
        // Kiểm tra xem tin nhắn có bắt đầu bằng tiền tố "Server: " không
        return message.startsWith("Server: ");
    }

    private void closeConnection() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}