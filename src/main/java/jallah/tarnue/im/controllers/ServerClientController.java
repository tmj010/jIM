package jallah.tarnue.im.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class ServerClientController {
    private static final Logger LOGGER = Logger.getLogger("ServerClientController");

    private static final String SERVER_FXML = "/fxml/server.fxml";
    private static final String CLIENT_LOGIN_FXML = "/fxml/client-login.fxml";

    @FXML
    private Button joinBtn;

    @FXML
    private Button hostBtn;

    @FXML
    private void startClient(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(CLIENT_LOGIN_FXML));

        Parent root = fxmlLoader.load();
        Scene clientLoginScene = new Scene(root);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(clientLoginScene);
        stage.show();
    }

    @FXML
    private void startServer(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(SERVER_FXML));

        Parent root = fxmlLoader.load();
        Scene serverScene = new Scene(root);

        ServerController serverController = fxmlLoader.getController();
        serverController.startServer();

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(serverScene);
        stage.show();
    }

}
