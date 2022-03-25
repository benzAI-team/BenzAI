package view.database.boxes;

import java.util.ArrayList;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sql.BenzenoidCriterion;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;
import view.database.DeleteButton;

public abstract class HBoxDatabaseCriterion extends HBox {

	protected boolean valid;

	protected DeleteButton deleteButton;
	protected ImageView warningIcon;

	protected DatabasePane parent;
	private ChoiceBoxDatabaseCriterion choiceBoxCriterion;

	public HBoxDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(5.0);

		this.parent = parent;
		this.choiceBoxCriterion = choiceBoxCriterion;

		warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		deleteButton = new DeleteButton(this);

		Tooltip.install(warningIcon, new Tooltip("Invalid entry, criterion will not be considered"));
		Tooltip.install(deleteButton, new Tooltip("Delete criterion"));

		deleteButton.setOnAction(e -> {
			parent.removeCriterion(choiceBoxCriterion, this);
		});

		initialize();
	}

	protected abstract void checkValidity();

	protected abstract void initialize();

	public abstract ArrayList<BenzenoidCriterion> buildCriterions();

	public boolean isValid() {
		return valid;
	}

}
