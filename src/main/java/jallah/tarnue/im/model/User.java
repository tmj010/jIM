package jallah.tarnue.im.model;

import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;

public class User implements Comparable<User>, Serializable {
    private final String userName;
    private transient final Socket socket;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userName.equals(user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public int compareTo(User o) {
        return userName.compareTo(o.userName);
    }
}
