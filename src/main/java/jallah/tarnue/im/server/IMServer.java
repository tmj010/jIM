package jallah.tarnue.im.server;

import jallah.tarnue.im.Protocol;
import jallah.tarnue.im.exception.IMUserCreationException;
import jallah.tarnue.im.model.User;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class IMServer {
    private static final Logger LOGGER = Logger.getLogger("IMServer");

    private final AtomicBoolean isServerRunning = new AtomicBoolean(Boolean.TRUE);
    private final AtomicInteger threadCount = new AtomicInteger(1);
    private final ExecutorService executorService;
    private final List<User> users;
    private ObservableList<String> userNames;

    public IMServer() {
        executorService = Executors.newFixedThreadPool(5, new IMServerThreadFactory());
        users = Collections.synchronizedList(new ArrayList<>());
    }

    public void startServer() {
        LOGGER.info("[71942afe-9a4e-43b4-98c8-5eb82ac986db] Starting server");
        executorService.submit(new ServerHandler());
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUserNames(ObservableList<String> userNames) {
        this.userNames = userNames;
    }

    public Boolean isServerUp() {
        return isServerRunning.get();
    }

    public void shutServerDown() throws InterruptedException {
        LOGGER.info("[c4bc4ff2-82ef-4264-9565-3f12095a88d1] about to shut server down, is server up: " + isServerRunning.get());
        if (isServerRunning.get()) {
            isServerRunning.setPlain(Boolean.FALSE);
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }
    }

    private class ServerHandler extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            try (ServerSocket serverSocket = new ServerSocket(Protocol.SERVER_PORT)) {
                LOGGER.info("[04d3d437-a68f-4d14-b7e5-44514c833694] Server is up and running");

                while (isServerRunning.get()) {
                    Socket userSocket = serverSocket.accept();
                    User newUser = createNewUser(userSocket);
                    users.add(newUser);
                }

                LOGGER.info("[8d86bab8-59eb-43fc-8027-1859eb74e211] Server has been shut down");
            } catch (IOException | IMUserCreationException e) {
                LOGGER.severe("[3ac54f37-aca1-413c-86d0-9337a74d7b72] error in server " + e.getMessage());
                LOGGER.severe("[c4b69445-05a2-4235-a05b-f90301841372] Shutting down server");
                try {
                    shutServerDown();
                } catch (InterruptedException interruptedException) {
                    LOGGER.severe("[658e37be-63c3-4e60-956c-c0d3ca1f09b8] error while shutting down");
                }
            }

            return null;
        }

        private User createNewUser(Socket userSocket) throws IMUserCreationException {
            User newUser = null;
            StringBuilder userNameBuilder = new StringBuilder();
            try (var reader = new BufferedReader(
                    new InputStreamReader(userSocket.getInputStream(), StandardCharsets.UTF_8))) {
                int userName = 0;
                while ((userName = reader.read()) != Protocol.DONE) {
                    userNameBuilder.append((char) userName);
                }
                newUser = new User(userNameBuilder.toString(), userSocket);
                userNames.add(userNameBuilder.toString());
                LOGGER.info("[94dd70d0-ec0e-47e3-a921-49c957a9c321] user: " + userNameBuilder + " join jIM!");
            } catch (Exception e) {
                LOGGER.severe("[44c4127f-9087-425f-bdea-87366fcea41e] error while creating new user " + e.getMessage());
                throw new IMUserCreationException(e.getMessage());
            }
            return newUser;
        }
    }

    private class IMServerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "IMServer-thread-" + threadCount.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}
