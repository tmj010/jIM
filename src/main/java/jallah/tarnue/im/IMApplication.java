package jallah.tarnue.im;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IMApplication extends Application {
    private static final String SERVER_CLIENT_FXML = "/fxml/server-client.fxml";
    private static final String TITLE = "jIM";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(SERVER_CLIENT_FXML));
        Scene clientServerScene = new Scene(root);
        stage.setTitle(TITLE);
        stage.setScene(clientServerScene);
        stage.show();
    }
}
