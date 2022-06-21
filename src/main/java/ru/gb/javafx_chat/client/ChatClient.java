package ru.gb.javafx_chat.client;
import javafx.application.Platform;
import ru.gb.javafx_chat.Command;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import static ru.gb.javafx_chat.Command.*;

public class ChatClient {
    private final ChatController controller;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ChatClient(ChatController controller) {
        this.controller = controller;
    }

    public void openConnection() throws IOException {
        socket = new Socket("127.0.0.1", 8489);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        new Thread(() ->{
            try {
                waitAuth();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                closeConnection();
            }
        }).start();
    }

    private void waitAuth() throws IOException {
        while (true){
            final String message = in.readUTF();
            final Command command = getCommand(message);
            final String[] params = command.parse(message);
            if (command == AUTHOK) {
                final String nick = params[0];
                controller.setAuth(true);
                controller.addMessage(nick + " успешно авторизовался");
                break;
            }
        }
    }

    private void closeConnection() {
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

    private void readMessages() throws IOException {
        while (true){
           final String message = in.readUTF();
           final Command com = Command.getCommand(message);
            if (END == com){
                controller.setAuth(false);
                break;
            }
            final String[] params = com.parse(message);
            if (ERROR == com){
                String messageError = params[0];
                Platform.runLater(() -> controller.showError(messageError));
                continue;
            }
            if (MESSAGE == com){
                Platform.runLater(() -> controller.addMessage(params[0]));
            }
            if(CLIENTS == com){
                Platform.runLater(() -> controller.updateClientList(params));
            }
        }
    }

    void sendMessage(Command command, String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
