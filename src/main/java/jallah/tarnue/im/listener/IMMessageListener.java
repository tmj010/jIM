package jallah.tarnue.im.listener;

public interface IMMessageListener {
    void processMessage(String tab, String username, String msg);
}
