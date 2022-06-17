module ru.gb.javafx_chat {
    requires javafx.controls;
    requires javafx.fxml;

    exports ru.gb.javafx_chat.client;
    opens ru.gb.javafx_chat.client to javafx.fxml;
}