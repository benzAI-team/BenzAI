package view.ames_format;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;

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
        deleteButton = new Button();

        deleteButton.resize(25, 25);
        deleteButton.setStyle("-fx-background-color: transparent;");

        Image imageAddButton;

        imageAddButton = new Image("/resources/graphics/icon-delete.png");

        ImageView view = new ImageView(imageAddButton);
        deleteButton.setPadding(new Insets(0));
        deleteButton.setGraphic(view);

        this.getChildren().addAll(fileLabel, deleteButton);
        setActions();
    }

    private void setActions() {
        deleteButton.setOnAction(e -> {
            try {
                delete();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public File getFile() {
        return file;
    }

    public int getIndex() {
        return index;
    }

    private void delete() throws IOException {
        parent.deleteFile(index);
    }
}
