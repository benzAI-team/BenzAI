package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxFloatDatabaseCriterion extends HBoxInDatabase {

	public HBoxFloatDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion, Subject subject, String possible_operators) {
		super(parent, choiceBoxCriterion, subject, possible_operators);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {

			String value = fieldValue1.getText();
			if ("IN".equals(operatorChoiceBox.getSelectionModel().getSelectedItem()))
				value = value + " " + fieldValue2.getText();

			criterions.add(new BenzenoidCriterion(getSubject(),
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), value));
			System.out.println(criterions.get(0));
		}

		return criterions;
	}

}
