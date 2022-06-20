package ru.gb.javafx_chat.server;

public record ServerLauncher() {
    public static void main(String[] args) {
        new ChatServer().run();
    }
}
