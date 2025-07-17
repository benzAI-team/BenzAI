package view.patterns;

public class PropertyCloseButton extends CloseButton {

    public PropertyCloseButton(PatternsEditionPane parent, int index) {
        super(parent, index);
        setVisible(false);
        setManaged(false);
    }
}
