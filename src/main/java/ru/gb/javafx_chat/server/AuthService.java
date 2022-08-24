package ru.gb.javafx_chat.server;

import java.io.Closeable;
import java.sql.SQLException;

public interface AuthService extends Closeable {
    String getNickByLoginAndPassword(String login, String password) throws SQLException;
    boolean registrationInChat(String nick, String login, String password) throws SQLException;
    boolean changeNickname(String nick, String password, String newPassword) throws SQLException;
}
