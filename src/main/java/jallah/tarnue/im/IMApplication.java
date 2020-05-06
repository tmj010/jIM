package jallah.tarnue.im;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class IMApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger("IMServer");

    private static final String SERVER_CLIENT_FXML = "/fxml/server-client.fxml";
    private static final String TITLE = "jIM";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(SERVER_CLIENT_FXML));

        Parent root = fxmlLoader.load();
        Scene serverClientScene = new Scene(root);

        stage.setTitle(TITLE);
        stage.setScene(serverClientScene);
        stage.show();
        LOGGER.info("[a0f957d9-2107-4612-b822-c3acb47f0dc4] started IMApplication!");
    }
}
