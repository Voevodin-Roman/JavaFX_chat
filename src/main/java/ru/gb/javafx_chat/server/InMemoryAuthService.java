package ru.gb.javafx_chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InMemoryAuthService implements AuthService {
    private static class UserData{
        private final String nick;
        private String login;
        private String password;

        public UserData(String nick, String login, String password) {
            this.nick = nick;
            this.login = login;
            this.password = password;
        }
        public UserData(String nick) {
            this.nick = nick;
        }
        public String getNick() {
            return nick;
        }
        public String getLogin() {
            return login;
        }
        public String getPassword() {
            return password;
        }
    }

    public Connection connectionToBase() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/pass.db");
        return connection;
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) throws SQLException {
        Statement statement = connectionToBase().createStatement();
        String request = "SELECT nick FROM users WHERE login=\"" + login + "\" AND password=\"" + password + "\"";
        System.out.println(request);
        ResultSet resultSet = statement.executeQuery(request);
        connectionToBase().close();
        if (resultSet.next()){
           return resultSet.getString("nick");
        }
        return null;
    }

    @Override
    public void close() {
        System.out.println("authentication service stopped");
    }

    @Override
    public void registrationInChat(String nick, String login, String password) throws SQLException {
        Statement statement = connectionToBase().createStatement();


        connectionToBase().close();

    }

    @Override
    public boolean changeNickname(String nick, String password, String newPassword) throws SQLException {
        Statement statement = connectionToBase().createStatement();


        connectionToBase().close();
        return false;
    }
}
