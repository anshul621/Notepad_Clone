module com.example.notepad {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    //requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.fxmisc.richtext;
    requires reactfx;
    opens com.example.notepad to javafx.fxml;
    exports com.example.notepad;
    exports com.example.notepadclone;
    opens com.example.notepadclone to javafx.fxml;
}