package ru.gb.javafx_chat.server;

import ru.gb.javafx_chat.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatServer {
    private final Map<String,ClientHandler> clients;
    public ChatServer() {
        this.clients = new HashMap<>();
    }

    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(50000);
            AuthService authService = new InMemoryAuthService()){
            while (true) {
                System.out.println("Waiting connection...");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this, authService);
                System.out.println("Client online");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void messageToClient(ClientHandler from, String nick, String message){
        final ClientHandler clientTo = clients.get(nick);
        if (clientTo == null){
            from.sendMessage(Command.ERROR, "Пользователь не авторизован");
            return;
        }
        clientTo.sendMessage(Command.MESSAGE, from.getNick() + " private message: " + message);
        from.sendMessage(Command.MESSAGE, "Пользователю " + nick + " : " + message);
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
        broadcastClientList();
    }

    private void broadcastClientList() {
        final String nick = clients.values().stream()
                .map(ClientHandler::getNick)
                .collect(Collectors.joining(" "));
        broadcast(Command.CLIENTS, nick);
    }

    public void broadcast(Command command, String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(command, message);
        }
    }

    public boolean isNickBusy(String nick) {
        return clients.get(nick) !=null;
    }

    public void unsubscribe(ClientHandler client) {
        //Убираем пользователя из списка клиентов при отключении клиента
        clients.remove(client.getNick());
        broadcastClientList();
    }
}
