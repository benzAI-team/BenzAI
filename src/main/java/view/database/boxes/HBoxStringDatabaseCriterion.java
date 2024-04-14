package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxStringDatabaseCriterion extends HBoxDatabaseCriterion {

	private TextField fieldValue;

	public HBoxStringDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion, Subject subject, String possible_operators) {
		super(parent, choiceBoxCriterion,subject, possible_operators);
	}

	@Override
	protected void checkValidity() {
		if (fieldValue.getText().length() == 0) {
			valid = false;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().addAll(warningIcon, deleteButton);
		}

		else {
			valid = true;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().add(deleteButton);
		}
	}

	@Override
	protected void initialize() {
		valid = false;

		fieldValue = new TextField();

		fieldValue.setOnKeyReleased(e -> {
			checkValidity();
		});

		this.getChildren().addAll(fieldValue, warningIcon, deleteButton);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new BenzenoidCriterion(getSubject(),
					BenzenoidCriterion.getOperator("="), fieldValue.getText()));
		}

		return criterions;
	}

}
