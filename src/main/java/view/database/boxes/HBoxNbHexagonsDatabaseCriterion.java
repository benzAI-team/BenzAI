package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxNbHexagonsDatabaseCriterion extends HBoxClassicDatabaseCriterion {

	public HBoxNbHexagonsDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new BenzenoidCriterion(Subject.NB_HEXAGONS,
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), fieldValue.getText()));
		}

		return criterions;
	}

}
