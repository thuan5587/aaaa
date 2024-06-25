package com.example.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private CryptoUtils cryptoUtils;

    public Server() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8088);
        cryptoUtils = new CryptoUtils();
        System.out.println("Server started");

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket, clients, cryptoUtils);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server();
    }
}
