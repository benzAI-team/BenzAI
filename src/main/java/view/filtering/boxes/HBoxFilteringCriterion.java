package view.filtering.boxes;

import java.util.ArrayList;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;

public abstract class HBoxFilteringCriterion extends HBox {

	protected boolean valid;
	protected ChoiceBoxFilteringCriterion choiceBoxCriterion;
	protected FilteringPane parent;

	protected ImageView warningIcon;
	protected FilteringDeleteButton deleteButton;

	public HBoxFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {

		super(5.0);

		this.parent = parent;
		this.choiceBoxCriterion = choiceBoxCriterion;

		warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		deleteButton = new FilteringDeleteButton(this);

		Tooltip.install(warningIcon, new Tooltip("Invalid entry, criterion will not be considered"));
		Tooltip.install(deleteButton, new Tooltip("Delete criterion"));

		deleteButton.setOnAction(e -> {
			parent.removeCriterion(choiceBoxCriterion, this);
		});

		initialize();
	}

	public boolean isValid() {
		return valid;
	}

	protected abstract void checkValidity();

	protected abstract void initialize();

	public abstract ArrayList<FilteringCriterion> buildCriterions();

}
