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
        return DriverManager.getConnection("jdbc:sqlite:src/main/resources/pass.db");
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) throws SQLException {
        Statement statement = connectionToBase().createStatement();
        String request = "SELECT nick FROM users WHERE login='" + login + "' AND password='" + password + "'";
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
    public boolean registrationInChat(String nick, String login, String password) {
        int insertNewUsers = 0;
        try {
            Statement statement = connectionToBase().createStatement();
            String parameters = "('%s', '%s', '%s')";
            String request = "INSERT INTO users(nick, login, password) VALUES " + String.format(parameters, nick, login, password);
            insertNewUsers = statement.executeUpdate(request);
            connectionToBase().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertNewUsers != 0;

    }

    @Override
    public boolean changeNickname(String newNick, String login, String password) {
        int updatedNick = 0;
        try {
            Statement statement = connectionToBase().createStatement();
            String request = "UPDATE users set nick='" + newNick + "'WHERE login='" + login + "' AND password='" + password + "'";
            updatedNick = statement.executeUpdate(request);
            connectionToBase().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedNick != 0;
    }
}
