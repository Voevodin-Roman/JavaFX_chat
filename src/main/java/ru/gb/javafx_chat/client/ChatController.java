package ru.gb.javafx_chat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;


    public void ClickSendButton() {
        final String message = messageArea.getText();
        if (message.isBlank()){
            return;
        }

        messageArea.appendText(message + "\n");
        messageField.clear();
        messageField.requestFocus();

    }
}