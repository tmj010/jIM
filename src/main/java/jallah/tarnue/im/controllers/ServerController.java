package jallah.tarnue.im.controllers;

import jallah.tarnue.im.Protocol;
import jallah.tarnue.im.listener.IMNewUserListener;
import jallah.tarnue.im.model.User;
import jallah.tarnue.im.server.IMServer;
import jallah.tarnue.im.server.IMServerClientHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class ServerController {
    private static final Logger LOGGER = Logger.getLogger("ServerController");
    private static final String SERVER_NAME = "Server: ";

    @FXML
    private Button btnSend;

    @FXML
    private TextArea txtAreaMsg;

    @FXML
    private TextFlow txtFlowBoard;

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
        server.setNewUserListener(this::newUserOperation);
        server.setMessageListener(this::processClientMsg);
    }

    @FXML
    private void sendMsg(ActionEvent event) {
        String msg = txtAreaMsg.getText();

        if (StringUtils.isNotBlank(msg)) {
            Text txtServerName = new Text(SERVER_NAME);
            txtServerName.setFill(Color.GREEN);

            Text msgTxt = new Text(msg + System.lineSeparator());
            txtFlowBoard.getChildren().addAll(txtServerName, msgTxt);

            txtAreaMsg.setText(StringUtils.EMPTY);

            sendMsgToUsers(msg);
        }
    }

    private void processClientMsg(String tab, String username, String msg) {
        Platform.runLater(() -> txtFlowBoard.getChildren().add(new Text(username + ": " + msg + System.lineSeparator())));
    }

    private void newUserOperation(String newUser, IMNewUserListener.UserOperation userOperation) {
        Platform.runLater(() -> {
            if (IMNewUserListener.UserOperation.ADD == userOperation) {
                userNames.add(newUser);
            } else {
                userNames.remove(newUser);
            }
        });
    }

    private void sendMsgToUsers(String msg) {
        if (CollectionUtils.isNotEmpty(server.getClientHandlers())) {
            LOGGER.info("[2e3cbda2-0f99-4e1d-9d38-cd54cb494ef0] About to send message to all clients!");
            server.getClientHandlers().parallelStream()
                    .map(IMServerClientHandler::getUser)
                    .map(User::getSocket)
                    .forEach(sendMsgToUserHelper.apply(msg));
        }
    }

    private final Function<String, Consumer<Socket>> sendMsgToUserHelper = msg -> socket -> {
        try {
            var toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            toClient.write(Protocol.FROM_SERVER);
            toClient.newLine();

            toClient.write(msg);
            toClient.newLine();

            toClient.flush();
        } catch (IOException e) {
            LOGGER.severe("[e22703f9-d2b0-4048-8927-6b14b31d7ac8] error while sending message to client: " + e.getMessage());
        }
    };

}
