module hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens hotel to javafx.fxml;
    opens hotel.ui to javafx.fxml;
    opens hotel.model to javafx.base;
    opens hotel.service to javafx.base;

    exports hotel;
    exports hotel.ui;
    exports hotel.model;
    exports hotel.service;
}
