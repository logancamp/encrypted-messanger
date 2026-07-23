module dev.krupp.passwordmanager {
    requires javafx.fxml;
    requires atlantafx.base;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens edu.cwru.messaging to javafx.fxml;
    opens edu.cwru.messaging.model to com.fasterxml.jackson.databind;
    exports edu.cwru.messaging;
    exports edu.cwru.messaging.utils;
    opens edu.cwru.messaging.utils to javafx.fxml;
}