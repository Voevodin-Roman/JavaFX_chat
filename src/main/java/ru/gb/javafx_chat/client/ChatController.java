package ru.gb.javafx_chat.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ru.gb.javafx_chat.Command;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ChatController {
    @FXML
    private ListView <String> clientList;
    @FXML
    private HBox aurhBox;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passField;
    @FXML
    private HBox messageBox;
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;

    private final ChatClient client;
    private String selectedNick;

    public ChatController() {
        this.client = new ChatClient(this);
        while (true) {
            try {
                client.openConnection();
                break;
            } catch (IOException e) {
                showNotification();
            }
        }
    }

    private void showNotification() {
       final Alert alert = new Alert(Alert.AlertType.ERROR, "Не могу подключится к серверу\n",
                new ButtonType("Повторить", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Выйти", ButtonBar.ButtonData.CANCEL_CLOSE)
        );
       alert.setTitle("Ошибка подключения");
        final Optional<ButtonType> answer = alert.showAndWait();
        final Boolean isExit = answer.map(select -> select.getButtonData().isCancelButton()).orElse(false);
        if (isExit){
            System.exit(0);
        }
    }

    public void singninBtnClick() {
        client.sendMessage(Command.AUTH,loginField.getText() + " " + passField.getText());
    }

    public void setAuth(boolean succes){
        aurhBox.setVisible(!succes);
        messageBox.setVisible(succes);
    }

    public void ClickSendButton() {
       final String message = messageField.getText();
       if (message.isBlank()){
           return;
       }
       if (selectedNick != null){
           client.sendMessage(Command.PRIVATE_MESSAGE,  message);
           selectedNick = null;
       }else {
           client.sendMessage(Command.MESSAGE, message);
       }
       messageField.clear();
       messageField.requestFocus();
    }

    public void addMessage(String message) {
        messageArea.appendText(message + "\n");
    }

    public void showError(String errorMessage) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage,
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        alert.setTitle("Error!");
        alert.showAndWait();
    }

    public void seleclClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            final String selectedNick = clientList.getSelectionModel().getSelectedItem();
            if (selectedNick != null && !selectedNick.isEmpty()) {
                this.selectedNick = selectedNick;
            }
        }
    }

    public void updateClientList(String[] clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);
    }
}