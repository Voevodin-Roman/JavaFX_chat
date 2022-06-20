package ru.gb.javafx_chat;

public enum Command {
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
            String[] split = commandText.split(TOKEN_DELIMITER, 3);
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
    };

    private final String command;
    static final String TOKEN_DELIMITER = "\\p{Blank}+";

    public String getCommand() {
        return command;
    }

    Command(String command) {
        this.command = command;
    }

    public static boolean isCommand(String message){
        return message.startsWith("/");
    }

    public static Command getCommand(String message){
        if (!isCommand(message)){
            throw new RuntimeException("Не верная команда: " + message);
        }
        String cmd = message.split(TOKEN_DELIMITER, 2)[0];
        for (Command command : values()){
            if (command.getCommand().equals(cmd)){
                return command;
            }
        }throw new RuntimeException("Не известная команда: " cmd)
    }



    public abstract String[] parse(String commandText);
}
