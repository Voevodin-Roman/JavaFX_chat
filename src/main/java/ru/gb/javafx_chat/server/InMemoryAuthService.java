package ru.gb.javafx_chat.server;

import java.io.IOException;
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
    private final List<UserData> users;

    public InMemoryAuthService() {
        users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(new UserData("nick" + i, "login" + i, "0" + i));
        }
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (login.equals(user.getLogin()) && password.equals(user.getPassword())){
                return user.getNick();
            }
        }
        return null;
    }

    @Override
    public void close() {
        System.out.println("authentication service stopped");
    }
}
