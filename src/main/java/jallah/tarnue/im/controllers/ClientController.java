package jallah.tarnue.im.controllers;

import jallah.tarnue.im.client.IMUserClient;
import jallah.tarnue.im.listener.IMNewUserListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;

public class ClientController {
    @FXML
    private TabPane chatTabs;

    @FXML
    private ListView<String> loginUsers;

    private final ObservableList<String> userNames = FXCollections.observableArrayList();

    private IMUserClient userClient;

    public void setUserClient(IMUserClient userClient) {
        this.userClient = userClient;
    }

    @FXML
    private void initialize() {
        loginUsers.setItems(userNames);
        userClient.setUserListener(this::newUserOperation);
    }

    private void newUserOperation(String userName, IMNewUserListener.UserOperation userOperation) {
        Platform.runLater(() -> {
            if (IMNewUserListener.UserOperation.ADD == userOperation) {
                userNames.add(userName);
            } else {
                userNames.remove(userName);
            }
        });
    }
}
