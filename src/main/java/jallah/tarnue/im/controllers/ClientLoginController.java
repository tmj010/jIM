package jallah.tarnue.im.controllers;

import jallah.tarnue.im.client.IMUserClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ClientLoginController {
    private static final Logger LOGGER = Logger.getLogger("ClientLoginController");

    private static final String SERVER_CLIENT_FXML = "/fxml/server-client.fxml";
    private static final String CLIENT_FXML = "/fxml/client.fxml";

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    @FXML
    private TextField txtFieldUsername;

    @FXML
    private TextField txtFieldHost;

    @FXML
    private TextField txtFieldPort;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnCancel;

    private IMUserClient userClient;

    @FXML
    private void login(ActionEvent event) {
        if (validate()) {
            try {
                userClient = new IMUserClient(txtFieldUsername.getText(), txtFieldHost.getText(), Integer.parseInt(txtFieldPort.getText()));
                executorService.execute(userClient);
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Successfully connected to the server", ButtonType.OK);
                successAlert.showAndWait().ifPresent(openClientWindow.apply(event, txtFieldUsername.getText()));
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Server is not up or invalid server data, please try again", ButtonType.OK);
                errorAlert.showAndWait();
                clearInputBoxes();
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Please enter valid information", ButtonType.OK);
            errorAlert.showAndWait();
            clearInputBoxes();
        }
    }

    private final BiFunction<ActionEvent, String, Consumer<ButtonType>> openClientWindow = (event, username) -> buttonType -> {
        try {
            if (ButtonType.OK == buttonType) {
                openWindow(event, username, CLIENT_FXML);
            }
        } catch (IOException e) {
            LOGGER.severe("[f83ebbf8-c649-4abe-b5d4-501cda77582e] Error while opening" + e.getMessage());
        }
    };

    @FXML
    private void cancel(ActionEvent event) throws IOException {
        clearInputBoxes();
        openWindow(event, StringUtils.EMPTY, SERVER_CLIENT_FXML);
    }

    private void openWindow(ActionEvent event, String username, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(fxml));

        Parent root = fxmlLoader.load();

        if (CLIENT_FXML.equalsIgnoreCase(fxml)) {
            ClientController clientController = fxmlLoader.getController();
            clientController.addIMUserClient(userClient);
        }

        Scene scene = new Scene(root);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        if (StringUtils.isNotBlank(username)) {
            stage.setTitle("jIM (" + username + ")");
        }
        stage.show();
    }

    private void clearInputBoxes() {
        txtFieldUsername.setText(StringUtils.EMPTY);
        txtFieldHost.setText(StringUtils.EMPTY);
        txtFieldPort.setText(StringUtils.EMPTY);
    }

    private boolean validate() {
        String useName = txtFieldUsername.getText();
        String host = txtFieldHost.getText();
        String port = txtFieldPort.getText();

        try {
            Integer.parseInt(port.trim());
        } catch (NumberFormatException e) {
            return false;
        }

        return StringUtils.isNotBlank(useName) && StringUtils.isNotBlank(host);
    }

}
