package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxDefaultDatabaseCriterion extends HBoxDatabaseCriterion {

	public HBoxDefaultDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		valid = false;
	}

	@Override
	protected void initialize() {
		this.getChildren().addAll(warningIcon, deleteButton);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		return null;
	}

}
