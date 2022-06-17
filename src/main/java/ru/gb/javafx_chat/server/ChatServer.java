package ru.gb.javafx_chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {



    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(8489)) {
            System.out.println("Waiting connection...");
            final Socket socket = serverSocket.accept();
            new ClientHandler(socket, this);
            System.out.println("Client online");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void broadcast(String message){

    }
}
