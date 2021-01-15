package jallah.tarnue.im.controllers;

import jallah.tarnue.im.client.IMUserClient;
import jallah.tarnue.im.listener.IMNewUserListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

    private static final String CLIENT_CHAT_FXML = "/fxml/client-chat.fxml";

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
    private void initialize() throws IOException {
        loginUsers.setItems(userNames);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(CLIENT_CHAT_FXML));

        Parent clientChatNode = fxmlLoader.load();
        ClientChatController controller = fxmlLoader.getController();
        clientChatNode.setUserData(controller);

        Tab serverTab = chatTabs.getTabs().get(0);
        serverTab.setContent(clientChatNode);
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
            Tab chatTab = chatTabs.getTabs().stream().filter(tab -> tab.getText().equalsIgnoreCase(username))
                    .findFirst().orElseThrow(() -> new RuntimeException("No such tap " + username));

            Node content = chatTab.getContent();
            if (content.getUserData() instanceof ClientChatController) {
                ClientChatController clientChatController = (ClientChatController) content.getUserData();
                clientChatController.displayMessage(username, msg);
            }
        });
    }
}
