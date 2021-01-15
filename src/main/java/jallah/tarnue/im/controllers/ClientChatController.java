package jallah.tarnue.im.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ClientChatController {
    @FXML
    private TextFlow msgArea;

    @FXML
    private TextArea msgBox;

    @FXML
    private Button sendBtn;

    public void displayMessage(String username, String msg) {
        Text text = new Text(username + ": " + msg + System.lineSeparator());
        msgArea.getChildren().add(text);
    }

}
