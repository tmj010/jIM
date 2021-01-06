package jallah.tarnue.im.listener;

public interface IMMessageListener {
    void processMessage(String username, String msg);
}
