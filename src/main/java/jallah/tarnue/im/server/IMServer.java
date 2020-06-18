package jallah.tarnue.im.server;

import jallah.tarnue.im.Protocol;
import jallah.tarnue.im.exception.IMUserCreationException;
import jallah.tarnue.im.listener.IMNewUserListener;
import jallah.tarnue.im.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class IMServer {
    private static final Logger LOGGER = Logger.getLogger("IMServer");

    private final static AtomicInteger threadCount = new AtomicInteger(1);

    private final ExecutorService executorService = Executors.newFixedThreadPool(5, new IMServerThreadFactory());
    private final List<User> users = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isServerRunning = new AtomicBoolean(Boolean.TRUE);

    private IMNewUserListener newUserListener;

    public void startServer() {
        LOGGER.info("[71942afe-9a4e-43b4-98c8-5eb82ac986db] Starting server");
        executorService.execute(new ServerHandler());
    }

    public void setNewUserListener(IMNewUserListener loginUserListener) {
        this.newUserListener = loginUserListener;
    }

    public List<User> getUsers() {
        return users;
    }

    public Boolean isServerUp() {
        return isServerRunning.get();
    }

    public void shutServerDown() {
        LOGGER.info("[c4bc4ff2-82ef-4264-9565-3f12095a88d1] about to shut server down, is server up: " + isServerRunning.get());
        if (isServerRunning.get()) {
            isServerRunning.setPlain(Boolean.FALSE);
            executorService.shutdownNow();
        }
    }

    private class ServerHandler implements Runnable {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(Protocol.SERVER_PORT)) {
                LOGGER.info("[04d3d437-a68f-4d14-b7e5-44514c833694] Server is up and running");

                while (isServerRunning.get()) {
                    Socket userSocket = serverSocket.accept();
                    User newUser = createNewUser(userSocket);
                    users.add(newUser);
                    //executorService.execute(new IMServerClientThread(userSocket));
                }

                LOGGER.info("[8d86bab8-59eb-43fc-8027-1859eb74e211] Server has been shut down");
            } catch (IOException | IMUserCreationException e) {
                LOGGER.severe("[3ac54f37-aca1-413c-86d0-9337a74d7b72] error in server " + e.getMessage());
                LOGGER.severe("[c4b69445-05a2-4235-a05b-f90301841372] Shutting down server");
                shutServerDown();
            }
        }

        private User createNewUser(Socket userSocket) throws IMUserCreationException {
            StringBuilder userNameBuilder = new StringBuilder();
            try (var reader = new BufferedReader(
                    new InputStreamReader(userSocket.getInputStream(), StandardCharsets.UTF_8))) {
                int name;
                while ((name = reader.read()) != Protocol.DONE) {
                    userNameBuilder.append((char) name);
                }
                User newUser = new User(userNameBuilder.toString(), userSocket);
                newUserListener.addNewUser(newUser.getUserName());
                LOGGER.info("[94dd70d0-ec0e-47e3-a921-49c957a9c321] user: " + userNameBuilder + " join jIM!");
                return newUser;
            } catch (Exception e) {
                LOGGER.severe("[44c4127f-9087-425f-bdea-87366fcea41e] error while creating new user " + e.getMessage());
                throw new IMUserCreationException(e.getMessage());
            }
        }
    }

    private static class IMServerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "IMServer-thread-" + threadCount.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}
