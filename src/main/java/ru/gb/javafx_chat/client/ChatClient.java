package ru.gb.javafx_chat.client;

import javafx.application.Platform;
import ru.gb.javafx_chat.Command;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
        socket = new Socket("127.0.0.1", 50000);
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
    String nick;

    private void waitAuth() throws IOException {
        while (true){
            final String message = in.readUTF();
            final Command command = getCommand(message);
            final String[] params = command.parse(message);
            if (END == command){
                controller.setAuth(false);
                controller.resetButtonStatus(true);
                break;
            }
            if (command == AUTHOK) {
                //После авторизации очищаем текстовое поле от ошибок
                controller.clearMessageArea();
                nick = params[0];
                controller.setAuth(true);
                controller.addMessage(nick + " успешно авторизовался");
                break;
            }
            if (command == ERROR) {
                Platform.runLater(() -> controller.showError(params[1]));
                continue;
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
           final Command com = getCommand(message);
            final String[] params = com.parse(message);
            if (END == com){
                controller.setAuth(false);
                controller.resetButtonStatus(true);
                String messageEnd = params[0];
                Platform.runLater(() -> controller.showError(messageEnd));
                continue;
            }
            if (ERROR == com){
                String messageError = params[0];
                Platform.runLater(() -> controller.showError(messageError));
                continue;
            }
            if (MESSAGE == com){
                Platform.runLater(() -> controller.addMessage(params[0]));
            }
            if (CLIENTS == com){
                Platform.runLater(() -> controller.updateClientList(params));
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

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }
}
