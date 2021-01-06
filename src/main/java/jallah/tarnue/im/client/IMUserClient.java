package jallah.tarnue.im.client;

import jallah.tarnue.im.Protocol;
import jallah.tarnue.im.listener.IMMessageListener;
import jallah.tarnue.im.listener.IMNewUserListener;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class IMUserClient implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("IMUserClient");

    private static final AtomicBoolean isConnected = new AtomicBoolean(true);
    private static final String SERVER = "server";

    private final List<String> userNames;
    private IMNewUserListener userListener;
    private IMMessageListener messageListener;
    private final String userName;
    private final Socket socket;

    public IMUserClient(String userName, String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.userName = userName;
        this.userNames = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (var toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                 var fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                toServer.write(userName);
                toServer.newLine();
                toServer.flush();

                LOGGER.info("[d9bb76ef-ab7f-4728-aa3d-bd4a8e0681df] Send for current users");

                while (isConnected.get()) {
                    String msgFromServer = fromServer.readLine();
                    LOGGER.info("Server said: " + msgFromServer);

                    if (StringUtils.isNotBlank(msgFromServer)) {
                        if (msgFromServer.equalsIgnoreCase(Protocol.NEW_USER)) {
                            LOGGER.info("[f792ed6c-04ef-448a-bfca-94bcac61cdda] Getting new user");
                            String newUsername = fromServer.readLine();
                            addNewUserName(newUsername);
                        } else if (msgFromServer.equalsIgnoreCase(Protocol.ADD_CURRENT_USERS)) {
                            String currentUserStr = fromServer.readLine();
                            LOGGER.info(String.format("[69fb7fde-4655-4ee8-affd-e6589490d21f] Adding already logged in users: %s", currentUserStr));
                            Arrays.stream(currentUserStr.split(","))
                                    .filter(this::isNewUserName)
                                    .forEach(this::addNewUserName);
                        } else if (msgFromServer.equalsIgnoreCase(Protocol.FROM_SERVER)) {
                            String msg = fromServer.readLine();
                            messageListener.processMessage(SERVER, msg);
                        }
                    }
                }

            } catch (IOException e) {
                LOGGER.severe("[0772f17c-6456-4d5c-b1a5-c0abb3cfbd43] error: " + e.getMessage());
            }
        }
    }

    public void setMessageListener(IMMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setUserListener(IMNewUserListener userListener) {
        this.userListener = userListener;
    }

    private void addNewUserName(String newUserName) {
        this.userNames.add(newUserName);
        this.userListener.addNewUser(newUserName, IMNewUserListener.UserOperation.ADD);
    }

    public void getAllCurrentUserNamesFromServer() throws IOException {
        var toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        toServer.write(Protocol.CURRENT_USERS);
        toServer.newLine();
        toServer.flush();
    }

    private boolean isNewUserName(String newUserName) {
        return StringUtils.isNotBlank(newUserName) && !this.userName.equalsIgnoreCase(newUserName)
                && !userNames.contains(newUserName);
    }
}
