package view.database.boxes;

import database.BenzenoidCriterion;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;
import view.database.DeleteButton;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class HBoxDatabaseCriterion extends HBox {

	protected boolean valid;

	protected DeleteButton deleteButton;
	protected ImageView warningIcon;

	protected DatabasePane parent;
	private final ChoiceBoxDatabaseCriterion choiceBoxCriterion;
	private String name;
	private ArrayList<String> possible_operators;

	public HBoxDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion, String name, String possible_operators) {
		super(5.0);

		this.parent = parent;
		this.choiceBoxCriterion = choiceBoxCriterion;
		this.name = name;
		if (possible_operators != null) {
		  this.possible_operators = new ArrayList<>(Arrays.asList(possible_operators.split(" ")));
		}
		else {
		  this.possible_operators = null;
		}

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
  
	public String getName() {
    return this.name;
  }

	public ArrayList<String> get_possible_operators() {
    return this.possible_operators;
  }

}
