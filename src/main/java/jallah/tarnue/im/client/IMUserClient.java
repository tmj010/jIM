package jallah.tarnue.im.client;

import jallah.tarnue.im.Protocol;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class IMUserClient implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("IMUserClient");

    private static final AtomicBoolean isConnected = new AtomicBoolean(true);
    private final String userName;
    private final Socket socket;

    public IMUserClient(String userName, String host, int port) throws IOException {
        this.userName = userName;
        this.socket = new Socket(host, port);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (var toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                var fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                toServer.write(userName);
                toServer.write(Protocol.DONE);

                while (isConnected.get()) {
                    String msgFromServer = fromServer.readLine();
                    if (StringUtils.isNotBlank(msgFromServer)) {
                        LOGGER.info("Server said: " + msgFromServer);
                    }
                }

            } catch (IOException e) {
                LOGGER.severe("[0772f17c-6456-4d5c-b1a5-c0abb3cfbd43] error: " + e.getMessage());
            }
        }
    }
}
