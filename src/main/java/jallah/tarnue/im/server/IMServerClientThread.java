package jallah.tarnue.im.server;

import jallah.tarnue.im.Protocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class IMServerClientThread implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("IMServerClientThread");

    private AtomicBoolean online = new AtomicBoolean(true);
    private Socket socket;

    public IMServerClientThread(Socket socket) {
        LOGGER.info("[70adf93f-b0ef-4974-a647-d62ccbf1fdbc] inside IMServerClientThread");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (online.get()) {
                var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                LOGGER.info("[d2a0d5f8-b3a0-4b37-8c9c-13e454118a4d] Got reader!!");
                String instruction;
                while ((instruction = reader.readLine()) != null) {
                    LOGGER.info("[1a6e6ea9-c9b5-4ef6-a9bd-f2e822080cb0] instruction: " + instruction);
                    switch (instruction) {
                        case Protocol.NEW_USER: //TODO after testing, remove
                            LOGGER.info("[e411c583-f303-4b5b-b5e4-724861ea7ae2] user name: " + reader.readLine());
                            System.out.println("hello");
                        default:
                            LOGGER.info("[daf27a1d-5601-40c0-9c55-6d619f0f7817] Not a valid instruction: " + instruction);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("[755085b2-9ffe-4fea-ba49-fc5d5ccff741] error in IMServerClientThread: " + e.getMessage());
        }
    }
}
