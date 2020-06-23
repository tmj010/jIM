package jallah.tarnue.im.server;

import jallah.tarnue.im.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class IMServerClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("IMServerClientThread");

    private final AtomicBoolean online = new AtomicBoolean(true);
    private final Socket socket;
    private List<User> users;

    public IMServerClientHandler(List<User> users, Socket socket) {
        LOGGER.info("[70adf93f-b0ef-4974-a647-d62ccbf1fdbc] inside IMServerClientThread");
        this.users = users;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            try (var fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 var toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                toClient.write("Welcome" + System.lineSeparator());
                toClient.flush();

                LOGGER.info("[d2a0d5f8-b3a0-4b37-8c9c-13e454118a4d] Got reader!!");
                while (online.get()) {
                    int instruction = fromClient.read();
                    LOGGER.info("Client said: " + (char) instruction);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("[755085b2-9ffe-4fea-ba49-fc5d5ccff741] error in IMServerClientThread: " + e.getMessage());
        }
    }
}
