package view.database.boxes;

import java.util.ArrayList;

import sql.BenzenoidCriterion;
import sql.BenzenoidCriterion.Subject;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxNbCarbonsDatabaseCriterion extends HBoxClassicDatabaseCriterion {

	public HBoxNbCarbonsDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new BenzenoidCriterion(Subject.NB_CARBONS,
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), fieldValue.getText()));

		}

		return criterions;
	}

}
