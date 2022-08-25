package ru.gb.javafx_chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.gb.javafx_chat.Command;

import java.io.IOException;

public class ChatClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatClientApp.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Chat Client");
        stage.setScene(scene);
        stage.show();
        ChatController chatController = fxmlLoader.getController();
        stage.setOnCloseRequest(event -> chatController.getClient().sendMessage(Command.END));
    }

    public static void main(String[] args) {
        launch();
    }
}