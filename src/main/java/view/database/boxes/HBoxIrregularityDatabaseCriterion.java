package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxIrregularityDatabaseCriterion extends HBoxInDatabase {

	public HBoxIrregularityDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {

			String value = fieldValue1.getText();
			if ("IN".equals(operatorChoiceBox.getSelectionModel().getSelectedItem()))
				value = value + " " + fieldValue2.getText();

			// String value = fieldValue1.getText() + " " + fieldValue2.getText();
			criterions.add(new BenzenoidCriterion(Subject.IRREGULARITY,
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), value));
			System.out.println(criterions.get(0));
		}

		return criterions;
	}

}
