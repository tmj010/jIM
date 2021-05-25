package jallah.tarnue.im.model;

import java.io.Serializable;
import java.net.Socket;

public record User(String userName, Socket socket) implements Comparable<User>, Serializable {
    @Override
    public int compareTo(User o) {
        return userName.compareTo(o.userName);
    }
}
