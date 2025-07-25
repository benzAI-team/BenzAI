package view.patterns;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

abstract class CloseButton extends Button {

    private int index;

    public CloseButton(PatternsEditionPane parent, int index) {
        this.index = index;

        Image image;

        image = new Image("/resources/graphics/close_button.png");
        ImageView view = new ImageView(image);
        this.resize(30, 30);
        this.setPadding(new Insets(0));
        this.setGraphic(view);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "CloseButton: " + index;
    }
}
