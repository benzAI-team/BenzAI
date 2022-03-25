package view.database.boxes;

import java.util.ArrayList;

import sql.BenzenoidCriterion;
import sql.BenzenoidCriterion.Subject;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxNbHydrogensDatabaseCriterion extends HBoxClassicDatabaseCriterion {

	public HBoxNbHydrogensDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new BenzenoidCriterion(Subject.NB_HYDROGENS,
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), fieldValue.getText()));
		}

		return criterions;
	}

}
