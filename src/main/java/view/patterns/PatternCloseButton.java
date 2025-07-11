package view.patterns;

import utils.Utils;

public class PatternCloseButton extends CloseButton {

	public PatternCloseButton(PatternsEditionPane parent, int index) {
		super(parent, index);

		this.setOnAction(e -> {
			if (parent.getNbItems() == 1) {
				Utils.alert("You cannot delete the last pattern.");
			} else {
				parent.getPatternListBox().removeEntry(index);
			}
		});
	}
}
