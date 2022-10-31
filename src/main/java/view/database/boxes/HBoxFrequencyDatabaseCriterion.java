package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxFrequencyDatabaseCriterion extends HBoxIRDatabase {

	public HBoxFrequencyDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {
			String value = fieldValue1.getText() + " " + fieldValue2.getText();
			criterions.add(new BenzenoidCriterion(Subject.FREQUENCY,
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), value));
			System.out.println(criterions.get(0));
		}

		return criterions;
	}

}
