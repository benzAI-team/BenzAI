package view.database.boxes;

import database.BenzenoidCriterion;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

import java.util.ArrayList;

public class HBoxDefaultDatabaseCriterion extends HBoxDatabaseCriterion {

	public HBoxDefaultDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion, "", null);
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
