package ru.gb.javafx_chat.client;

import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ru.gb.javafx_chat.Command;

public class ChatController {
    @FXML
    public Button endButton;
    @FXML
    public Button resetButton;
    @FXML
    public Button sendButton;
    @FXML
    public Button singInButton;
    @FXML
    public Button registration;
    @FXML
    public TextField nickField;
    @FXML
    public Button registrationButton;
    @FXML
    public Button changeNick;
    @FXML
    private ListView <String> clientList;
    @FXML
    private HBox authBox;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passField;
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

    public void signinBtnClick() {
         client.sendMessage(Command.AUTH,loginField.getText() + " " + passField.getText());
    }

    public void registration() {
        regestrationButonStatus(true);


    }
    public void registrationBtnClick() {
        client.sendMessage(Command.REG,loginField.getText() + " " + passField.getText() + " " + nickField.getText());
    }

    public void changedNick(ActionEvent actionEvent) {
    }

    public void setAuth(boolean succes){
        authBox.setVisible(!succes);
        endButton.setVisible(succes);
        clientList.setVisible(succes);
        messageField.setVisible(succes);
        sendButton.setVisible(succes);
        registration.setVisible(!succes);
        registrationButton.setVisible(!succes);
        nickField.setVisible(!succes);
    }

    public void resetButtonStatus(boolean succes){
        resetButton.setVisible(succes);
        singInButton.setVisible(!succes);
        registration.setVisible(!succes);
        registrationButton.setVisible(!succes);

    }

    public void regestrationButonStatus(boolean succes){
        nickField.setVisible(succes);
        registrationButton.setVisible(succes);
        singInButton.setVisible(!succes);
    }

    public void clickSendButton() {
       final String message = messageField.getText();
       if (message.isBlank()){
           return;
       }
       if (selectedNick != null){
           client.sendMessage(Command.PRIVATE_MESSAGE, selectedNick, message);
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
        //Вместо аллерта выводим ошибки в текстовое поле
        // final Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage,
        //         new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        // alert.setTitle("Error!!!!!!!!");
        // alert.showAndWait();
        addMessage("Error:  " + errorMessage);
    }

    public void selectClient(MouseEvent mouseEvent) {
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

    public void endClick() {
        messageArea.clear();
        client.sendMessage(Command.END);
    }
    public void clearMessageArea(){
        messageArea.clear();
    }

    public void resetConnection() {
        resetButtonStatus(false);
        try {
            client.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatClient getClient() {
        return client;
    }



}