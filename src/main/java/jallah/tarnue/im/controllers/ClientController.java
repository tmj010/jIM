package jallah.tarnue.im.controllers;

import jallah.tarnue.im.client.IMUserClient;
import jallah.tarnue.im.listener.IMNewUserListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.util.logging.Logger;

public class ClientController {
    private static final Logger LOGGER = Logger.getLogger("ClientController");

    @FXML
    private TabPane chatTabs;

    @FXML
    private ListView<String> loginUsers;

    private final ObservableList<String> userNames = FXCollections.observableArrayList();

    private IMUserClient userClient;

    public void addIMUserClient(IMUserClient client) {
        try {
            this.userClient = client;
            this.userClient.setUserListener(this::newUserOperation);
            this.userClient.getAllCurrentUserNamesFromServer();
        } catch (IOException e) {
            LOGGER.severe(String.format("[d1367138-c9ba-4f5f-87bd-649a5f9ad646] error while adding IMUserClient: %s", e.getMessage()));
        }
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
