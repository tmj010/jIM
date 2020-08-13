package jallah.tarnue.im.server;

import jallah.tarnue.im.Protocol;
import jallah.tarnue.im.model.User;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IMServerClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("IMServerClientThread");

    private final AtomicBoolean online = new AtomicBoolean(true);
    private final List<IMServerClientHandler> clientHandlers;
    private final User user;

    public IMServerClientHandler(User user, List<IMServerClientHandler> clientHandlers) {
        LOGGER.info("[70adf93f-b0ef-4974-a647-d62ccbf1fdbc] inside IMServerClientThread");
        this.clientHandlers = clientHandlers;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try {
            Socket socket = user.getSocket();
            try (var fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 var toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

                while (online.get()) {
                    String instruction = fromClient.readLine();

                    if (StringUtils.isNotBlank(instruction)) {
                        LOGGER.info(String.format("[01e9acd9-6258-488f-a375-0de817f03e87] User wants to: %s", instruction));

                        if (instruction.equalsIgnoreCase(Protocol.CURRENT_USERS)) {
                            toClient.write(Protocol.ADD_CURRENT_USERS);
                            toClient.newLine();

                            String allUserNames = clientHandlers.stream()
                                    .map(IMServerClientHandler::getUser)
                                    .map(User::getUserName)
                                    .filter(userName -> !userName.equalsIgnoreCase(user.getUserName()))
                                    .collect(Collectors.joining(","));

                            toClient.write(allUserNames);
                            toClient.newLine();

                            toClient.flush();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("[755085b2-9ffe-4fea-ba49-fc5d5ccff741] error in IMServerClientThread: " + e.getMessage());
        }
    }
}
