package view.ames_format;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;

public class LogFileBox extends HBox {

    private AmesFormatPane parent;
    private File file;
    private Label fileLabel;
    private Button deleteButton;
    private int index;

    public LogFileBox(AmesFormatPane parent, File file, int index) {
        super(5.0);
        this.parent = parent;
        this.file = file;
        this.index = index;
        initialize();
    }

    private void initialize() {
        fileLabel = new Label(file.getName());
        deleteButton = new Button("X");
        this.getChildren().addAll(fileLabel, deleteButton);
    }

    public File getFile() {
        return file;
    }

    public int getIndex() {
        return index;
    }

}
