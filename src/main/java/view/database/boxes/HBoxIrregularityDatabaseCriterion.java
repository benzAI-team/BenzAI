package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxIrregularityDatabaseCriterion extends HBoxClassicDatabaseCriterion {

	public HBoxIrregularityDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new BenzenoidCriterion(Subject.IRREGULARITY,
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), fieldValue.getText()));
		}

		return criterions;
	}

}
