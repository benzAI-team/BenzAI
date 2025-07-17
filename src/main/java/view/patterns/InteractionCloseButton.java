package view.patterns;

public class InteractionCloseButton extends CloseButton {

    public InteractionCloseButton(PatternsEditionPane parent, int index) {
        super(parent, index);

        this.setOnAction(e -> {
                parent.getInteractionListBox().removeEntry(index);
        });
    }
}
