package jallah.tarnue.im.server;

import jallah.tarnue.im.Protocol;
import jallah.tarnue.im.listener.IMMessageListener;
import jallah.tarnue.im.model.User;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IMServerClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("IMServerClientThread");
    private static final String SERVER = "server";

    private final AtomicBoolean online = new AtomicBoolean(true);
    private IMMessageListener messageListener;
    private final List<IMServerClientHandler> clientHandlers;
    private final User user;

    public IMServerClientHandler(User user, List<IMServerClientHandler> clientHandlers) {
        LOGGER.info("[70adf93f-b0ef-4974-a647-d62ccbf1fdbc] inside IMServerClientThread");
        this.clientHandlers = clientHandlers;
        this.user = user;
    }

    public void setMessageListener(IMMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try {
            Socket socket = user.socket();
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
                                    .map(User::userName)
                                    .filter(userName -> !userName.equalsIgnoreCase(user.userName()))
                                    .collect(Collectors.joining(","));

                            toClient.write(allUserNames);
                            toClient.newLine();

                            toClient.flush();
                        } else if (instruction.equalsIgnoreCase(Protocol.FROM_CLIENT_TO_SERVER)) {
                            String username = fromClient.readLine();
                            String msg = fromClient.readLine();
                            messageListener.processMessage(SERVER, username, msg);

                            clientHandlers.stream()
                                    .filter(this::notUserName)
                                    .forEach(sendMsgToOtherClient.apply(msg));

                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("[755085b2-9ffe-4fea-ba49-fc5d5ccff741] error in IMServerClientThread: " + e.getMessage());
        }
    }

    private boolean notUserName(IMServerClientHandler clientHandler) {
        return !clientHandler.getUser().userName().equalsIgnoreCase(getUser().userName());
    }

    final private Function<String, Consumer<IMServerClientHandler>> sendMsgToOtherClient = msg -> clientHandler -> {
        try {
            Socket socket = clientHandler.getUser().socket();
            var toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            toClient.write(Protocol.FROM_CLIENT_SERVER_TAB);
            toClient.newLine();

            toClient.write(getUser().userName());
            toClient.newLine();

            toClient.write(msg);
            toClient.newLine();

            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}
