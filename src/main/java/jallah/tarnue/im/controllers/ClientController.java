package jallah.tarnue.im.controllers;

import jallah.tarnue.im.client.IMUserClient;
import jallah.tarnue.im.listener.IMNewUserListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;

import java.util.logging.Logger;

public class ClientController {
    private static final Logger LOGGER = Logger.getLogger("ClientController");

    @FXML
    private TabPane chatTabs;

    @FXML
    private ListView<String> loginUsers;

    private final ObservableList<String> userNames = FXCollections.observableArrayList();

    private IMUserClient userClient;

    public void setUserClient(IMUserClient userClient) {
        this.userClient = userClient;
        this.userClient.setUserListener(this::newUserOperation);
    }

    @FXML
    private void initialize() {
        loginUsers.setItems(userNames);
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
