package ru.gb.javafx_chat;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Command {
    NICK("/nick"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[1], split[2], split[3]};
        }
    },
    REG("/reg"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[1], split[2], split[3]};
        }
    },
    AUTH("/auth"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[1], split[2]};
        }
    },
    AUTHOK("/authok"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[1]};
        }
    },
    END("/end"){
        @Override
        public String[] parse(String commandText) {
            return new String[0];
        }
    },
    PRIVATE_MESSAGE("/w"){
        @Override
        public String[] parse(String commandText) {
            final String[] split = commandText.split(TOKEN_DELIMITER, 3);
            return new String[]{split[1], split[2]};
        }
    },
    CLIENTS("/clients"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            String[] nicks = new String[split.length - 1];
            for (int i = 0; i < nicks.length; i++) {
                nicks[i] = split[i +1];
            }
            return nicks;
        }
    },
    ERROR("error"){
        @Override
        public String[] parse(String commandText) {
                String[] split = commandText.split(TOKEN_DELIMITER, 2);
                return new String[]{split[0], split[1]};
        }
    },
    MESSAGE("/message"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER, 2);
            return new String[]{split[1]};
        }
    };

    private final String command;
    static final String TOKEN_DELIMITER = "\\p{Blank}+";
    static final Map<String, Command> commandMap = Arrays.stream(values()).collect(Collectors.toMap(Command::getCommand, Function.identity()));
            //Map.of(
            //"/auth", AUTH,
            //"/authok", AUTHOK,
            //"/end", END,
            //"/w", PRIVATE_MESSAGE,
            //"/clients", CLIENTS,
            //"/error", ERROR);
    public String getCommand() {
        return command;
    }

    public String collectMessage(String... params){
        return this.command + " " + String.join(" ", params);
    }

    Command(String command) {
        this.command = command;
    }
    //Этот метод тоже не нужен, у нас теперь всё команда
    /*public static boolean isCommand(String message){
        return message.startsWith("/");
    }*/

    public static Command getCommand(String message){
       // if (!isCommand(message)){
       //     throw new RuntimeException("Не верная команда: " + message);
       // }
        String cmd = message.split(TOKEN_DELIMITER, 2)[0];
        // return comandMap.get(cmd);
        //Так как у нас теперь всё команда, то данная проверка не нужна
        //if(command == null) {
        //    throw new RuntimeException("Не известная команда: " + cmd);
        //}
        return commandMap.get(cmd);
    }

    public abstract String[] parse(String commandText);
}
