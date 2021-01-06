package jallah.tarnue.im.controllers;

import jallah.tarnue.im.client.IMUserClient;
import jallah.tarnue.im.listener.IMNewUserListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
            userClient = client;
            userClient.setUserListener(this::newUserOperation);
            userClient.setMessageListener(this::processMessage);
            userClient.getAllCurrentUserNamesFromServer();
        } catch (IOException e) {
            LOGGER.severe(String.format("[d1367138-c9ba-4f5f-87bd-649a5f9ad646] error while adding IMUserClient: %s", e.getMessage()));
        }
    }

    @FXML
    private void initialize() {
        loginUsers.setItems(userNames);

        Tab serverTab = chatTabs.getTabs().get(0);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(new TextFlow());
        serverTab.setContent(borderPane);
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

    private void processMessage(String username, String msg) {
        Platform.runLater(() -> {
            Tab usernameTab = chatTabs.getTabs().stream().filter(tab -> tab.getText().equalsIgnoreCase(username))
                    .findFirst().orElseThrow(() -> new RuntimeException("No such tap " + username));

            Node content = usernameTab.getContent();
            if (content instanceof BorderPane) {
                BorderPane borderPane = (BorderPane) content;
                Node centerNode = borderPane.getCenter();
                if (centerNode instanceof TextFlow) {
                    TextFlow textFlow = (TextFlow) centerNode;
                    textFlow.getChildren().add(new Text(msg + System.lineSeparator()));
                }
            }
        });
    }
}
