package ru.gb.javafx_chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InMemoryAuthService implements AuthService {
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

    //Так как столбцы nick и login помечены флагом "Уникальные", то не может быть двух пользователей с одинаковым ником и логином.
    //По этому, если метод registrationInChat или changeNickname возвращает false, значит эти значения уже существуют.

    @Override
    public boolean registrationInChat(String nick, String login, String password) {
        int insertNewUsers = 0;
        try {
            Statement statement = connectionToBase().createStatement();
            String parameters = "('%s', '%s', '%s')";
            String request = "INSERT INTO users(nick, login, password) VALUES " + String.format(parameters, nick, login, password);
            insertNewUsers = statement.executeUpdate(request);
            connectionToBase().commit();
            connectionToBase().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertNewUsers != 0;
    }

    @Override
    public boolean changeNickname(String nick, String newNick) {
        int updatedNick = 0;
        try {
            Statement statement = connectionToBase().createStatement();
            String requestSel = "Select id From users WHERE nick='" + nick + "'";
            ResultSet resultSet = statement.executeQuery(requestSel);
            String request = "UPDATE users set nick='" + newNick + "' WHERE id ='" + resultSet.getString("id") + "'";
            updatedNick = statement.executeUpdate(request);
            connectionToBase().commit();
            connectionToBase().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedNick != 0;
    }
}
