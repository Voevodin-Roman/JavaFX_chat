package ru.gb.javafx_chat.server;

import ru.gb.javafx_chat.Command;

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
    private AuthService authService;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {
        try {
            this.authService = authService;
            this.server = server;
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            new  Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                }finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        while (true){
            try {
                final String message = in.readUTF();
                if (Command.isCommand(message)){
                    Command command = Command.getCommand(message);
                    if (command == Command.AUTH){
                    String[] params = command.parse(message);
                    String nick = authService.getNickByLoginAndPassword(params[0], params[1]);
                    if(nick != null){
                        if (server.isNickBusy(nick)) {
                            sendMessage(Command.ERROR, "Пользователь уже авторизованн");
                            continue;
                        }
                        sendMessage(Command.AUTHOK, nick);
                        this.nick = nick;
                        server.broadcast(nick + " вошел в чат");
                        server.subscribe(this);
                        break;
                    }else {
                        sendMessage(Command.ERROR, "Не верный логин или пароль");
                    }
                  }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));

    }

    private void closeConnection() {
        sendMessage(Command.END);
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
            server.unsubscribe(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
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
                if (Command.isCommand(message) && Command.getCommand(message) == Command.END){
                    break;
                    //Добавляем обработка личных сообщений
               //}else if(message.startsWith("/w ")){
               //    String[] split = message.split("\\p{Blank}+", 3);
               //    server.messageToClient(split[1], "Личное сообщение от " + nick + " : " + split[2]);
                }else {
                server.broadcast(nick + ":" + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
