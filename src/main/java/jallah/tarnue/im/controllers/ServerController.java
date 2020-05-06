package jallah.tarnue.im.controllers;

import jallah.tarnue.im.server.IMServer;
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

    private ObservableList<String> userNames = FXCollections.observableArrayList();

    private IMServer server;

    public ServerController() {
        server = new IMServer();
    }

    public void startServer() {
        server.setUserNames(userNames);
        server.startServer();
    }

    @FXML
    private void initialize() {
        loginUsers.setItems(userNames);
    }

    @FXML
    private void stopServer(ActionEvent event) {
        try {
            server.shutServerDown();
        } catch (InterruptedException e) {
            LOGGER.severe("[26dd01b7-06f0-4c75-9e5d-fc8673f36359] Error while shutting down server: " + e.getMessage());
        }
    }

}
