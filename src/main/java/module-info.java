module jIM {
    requires javafx.controls;
    requires javafx.fxml;

    opens fxml to javafx.fxml;
    exports jallah.tarnue.im;
}