package jallah.tarnue.im.listener;

@FunctionalInterface
public interface IMNewUserListener {
    enum UserOperation {
        ADD, REMOVE
    }

    void addNewUser(String userName, UserOperation userOperation);
}
