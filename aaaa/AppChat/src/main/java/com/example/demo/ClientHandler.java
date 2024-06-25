package com.example.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private CryptoUtils cryptoUtils;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients, CryptoUtils cryptoUtils) {
        this.socket = socket;
        this.clients = clients;
        this.cryptoUtils = cryptoUtils;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
        // Tách tên người gửi và tin nhắn
        String[] parts = message.split(": ", 2);
        if (parts.length == 2) {
            String sender = parts[0];
            String msgContent = parts[1];
            XMLMessageWriter.writeMessage(sender, msgContent);
        }
    }

    @Override
    public void run() {
        try {
            String check = in.readLine();
            if ("publicKey".equals(check)) {
                handleSignup();
            } else if ("login".equals(check)) {
                handleLogin();
            } else {
                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    broadcast(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
        }
    }

    private void handleSignup() throws IOException {
        out.println(cryptoUtils.getPublicKey());
        String userName = in.readLine();
        String gmail = in.readLine();
        String pass = in.readLine();
        String passWord = Encrypt.encoded(PasswordEncryptionSV.decryptData(pass, cryptoUtils.getPrivateKey()));

        String sqlInsert = "INSERT INTO useraccounts (UserName, Password, Email) VALUES (?, ?, ?)";
        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement prepareCheck = connect.prepareStatement(sqlInsert)) {
            prepareCheck.setString(1, userName);
            prepareCheck.setString(2, passWord);
            prepareCheck.setString(3, gmail);
            prepareCheck.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin() throws IOException {
        out.println(cryptoUtils.getPublicKey());
        String userName = in.readLine();
        String pass = in.readLine();
        String passWord = Encrypt.encoded(PasswordEncryptionSV.decryptData(pass, cryptoUtils.getPrivateKey()));

        String verifyLogin = "SELECT UserName, Password FROM useraccounts WHERE UserName = ? AND Password = ?";
        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement prepareCheck = connect.prepareStatement(verifyLogin)) {
            prepareCheck.setString(1, userName);
            prepareCheck.setString(2, passWord);
            try (ResultSet result = prepareCheck.executeQuery()) {
                if (result.next()) {
                    out.println("true");
                } else {
                    out.println("false");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
