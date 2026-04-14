module converter {
    exports model.converter;

    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires image4j;
    requires org.slf4j;
    requires javafx.swing;
    requires ch.qos.logback.classic;
    requires com.luciad.imageio.webp;

    opens converter to javafx.fxml;
    exports converter;
}