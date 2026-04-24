module converter {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires image4j;
    requires org.slf4j;
    requires javafx.swing;
    requires jave.core;
    requires net.coobird.thumbnailator;
    requires org.apache.commons.io;

    opens converter to javafx.fxml;
    exports model.converterImage;
    exports converter;
    exports model.utility;
    exports viewHelp;
}