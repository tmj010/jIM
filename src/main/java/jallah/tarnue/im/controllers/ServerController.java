package jallah.tarnue.im.controllers;

import jallah.tarnue.im.server.IMServer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.logging.Logger;

public class ServerController {
    private static final Logger LOGGER = Logger.getLogger("ServerController");

    @FXML
    private Button stopServerBtn;

    @FXML
    private ListView<String> loginUsers;

    private final ObservableList<String> userNames = FXCollections.observableArrayList();

    private final IMServer server;

    public ServerController() {
        server = new IMServer();
    }

    public void startServer() {
        server.startServer();
    }

    @FXML
    private void initialize() {
        loginUsers.setItems(userNames);
        server.setNewUserListener(this::addNewUser);
    }

    @FXML
    private void stopServer(ActionEvent event) {
        server.shutServerDown();
    }

    private void addNewUser(String newUser) {
        Platform.runLater(() -> userNames.add(newUser));
    }

}
