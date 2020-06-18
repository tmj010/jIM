module jallah.im {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.apache.commons.lang3;

    opens fxml to javafx.fxml;
    opens jallah.tarnue.im.controllers to javafx.fxml;

    exports jallah.tarnue.im;
    exports jallah.tarnue.im.controllers;
}