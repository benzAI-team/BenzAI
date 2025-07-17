package view.database.boxes;

import database.BenzenoidCriterion;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

import java.util.ArrayList;

public class HBoxFloatDatabaseCriterion extends HBoxInDatabase {

	public HBoxFloatDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion, String name, String possible_operators) {
		super(parent, choiceBoxCriterion, name, possible_operators);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {

			String value = fieldValue1.getText();
			if ("IN".equals(operatorChoiceBox.getSelectionModel().getSelectedItem()))
				value = value + " " + fieldValue2.getText();

			criterions.add(new BenzenoidCriterion(getName(),
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), value));
			System.out.println(criterions.get(0));
		}

		return criterions;
	}

}
