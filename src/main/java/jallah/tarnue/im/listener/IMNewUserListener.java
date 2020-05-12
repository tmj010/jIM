package jallah.tarnue.im.listener;

@FunctionalInterface
public interface IMNewUserListener {
    void addNewUser(String userName);
}
