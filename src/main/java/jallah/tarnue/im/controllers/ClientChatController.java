package jallah.tarnue.im.controllers;

import jallah.tarnue.im.client.IMUserClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ClientChatController {
    @FXML
    private TextFlow msgArea;

    @FXML
    private TextArea msgBox;

    @FXML
    private Button sendBtn;

    private IMUserClient userClient;

    public void displayMessage(String username, String msg) {
        Text text = new Text(username + ": " + msg + System.lineSeparator());
        msgArea.getChildren().add(text);
    }

    public void setUserClient(IMUserClient userClient) {
        this.userClient = userClient;
    }

    @FXML
    private void sendMsg(ActionEvent event) throws IOException {
        String msg = msgBox.getText();
        if (StringUtils.isNotBlank(msg)) {
            String userName = userClient.getUser().userName();
            Text text = new Text(userName + ": " + msg + System.lineSeparator());
            msgArea.getChildren().add(text);

            msgBox.clear();

            userClient.sendMsg(msg);
        }
    }
}
