package ru.gb.javafx_chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;


    public ClientHandler(Socket socket, ChatServer server) {

        try {
            this.server = server;
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            new  Thread(() -> {
                try {
                    readMessage();
                }finally {
                    closeConnection();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        sendMessage("/end");
        if (in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessage() {
        while (true){
            try {
                final  String message = in.readUTF();
                if ("/end".equals(message)){
                    break;
                }
                server.broadcast(nick + ":" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
