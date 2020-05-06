package jallah.tarnue.im.client;

import jallah.tarnue.im.Protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class IMUserClient {
    private static final Logger LOGGER = Logger.getLogger("IMUserClient");

    private final String userName;
    private final String host;
    private final int port;
    private Socket socket;

    public IMUserClient(String userName, String host, int port) {
        this.userName = userName;
        this.host = host;
        this.port = port;
    }

    public void connectToServer() throws IOException {
        socket = new Socket(host, port);
        socket.getOutputStream().write(userName.getBytes()); // send user name to server
        socket.getOutputStream().write(Protocol.DONE);
        LOGGER.info("[8109955e-ed0a-475b-85d8-a77a325154cc] Connected to server and send userName");
    }

}
