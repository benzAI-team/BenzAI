package view.patterns;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

public class PropertyCloseButton extends Button {

    private int index;

    public PropertyCloseButton(PatternsEditionPane parent, int index, boolean print) {

        this.index = index;

        if (print) {
            Image image;

            image = new Image("/resources/graphics/close_button.png");

            ImageView view = new ImageView(image);

            this.resize(30, 30);

            this.setPadding(new Insets(0));

            this.setGraphic(view);

            this.setOnAction(e -> {
                if (parent.getNbItems() == 1) {
                    // TODO check whether is the last one related to a given pattern
                    Utils.alert("You cannot delete the last property on a pattern.");
                } else {
                    parent.getPatternListBox().removeEntry(index);
                }
            });
        }
        else {
            setVisible(false);
            setManaged(false);
        }
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
