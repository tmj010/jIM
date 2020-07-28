package jallah.tarnue.im.client;

import jallah.tarnue.im.Protocol;
import jallah.tarnue.im.listener.IMNewUserListener;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class IMUserClient implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("IMUserClient");

    private static final AtomicBoolean isConnected = new AtomicBoolean(true);
    private final List<String> userNames;
    private IMNewUserListener userListener;
    private final String userName;
    private final Socket socket;

    public IMUserClient(String userName, String host, int port) throws IOException {
        this.userName = userName;
        this.socket = new Socket(host, port);
        userNames = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (var toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                 var fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                toServer.write(userName);
                toServer.write(Protocol.DONE);
                toServer.flush();

                while (isConnected.get()) {
                    String msgFromServer = fromServer.readLine();
                    LOGGER.info("Server said: " + msgFromServer);

                    if (StringUtils.isNotBlank(msgFromServer)) {
                        if (msgFromServer.equalsIgnoreCase(Protocol.NEW_USER)) {
                            LOGGER.info("[f792ed6c-04ef-448a-bfca-94bcac61cdda] Getting new user");
                            String newUsername = fromServer.readLine();
                            userNames.add(newUsername);
                            LOGGER.info("[c9a439b9-06d7-49e3-9322-71bf58be92a0] Got new user; " + newUsername + " just join jIM");
                            userListener.addNewUser(newUsername, IMNewUserListener.UserOperation.ADD);
                        }
                    }
                }

            } catch (IOException e) {
                LOGGER.severe("[0772f17c-6456-4d5c-b1a5-c0abb3cfbd43] error: " + e.getMessage());
            }
        }
    }

    public void setUserListener(IMNewUserListener userListener) {
        this.userListener = userListener;
    }
}
