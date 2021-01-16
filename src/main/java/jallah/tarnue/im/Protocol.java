package jallah.tarnue.im;

public interface Protocol {
    int SERVER_PORT = 2665;
    int DONE = '~';
    String FROM_SERVER = "FROM_SERVER";
    String FROM_CLIENT_TO_SERVER = "FROM_CLIENT_TO_SERVER";
    String FROM_CLIENT_SERVER_TAB = "FROM_CLIENT_SERVER_TAB";
    String NEW_USER = "NEW_USER";
    String CURRENT_USERS = "CURRENT_USERS";
    String ADD_CURRENT_USERS = "ADD_CURRENT_USERS";
}
