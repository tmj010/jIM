package jallah.tarnue.im.model;

import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private String userName;
    private transient Socket socket;

    public User(String userName, Socket socket) {
        this.userName = userName;
        this.socket = socket;
    }

    public String getUserName() {
        return userName;
    }

    public Socket getSocket() {
        return socket;
    }
}
