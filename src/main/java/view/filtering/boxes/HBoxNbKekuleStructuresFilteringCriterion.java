package view.filtering.boxes;

import java.util.ArrayList;

import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.FilteringOperator;
import view.filtering.criterions.NbKekuleStructuresCriterion;

public class HBoxNbKekuleStructuresFilteringCriterion extends HBoxClassicalFilteringCriterion {

	public HBoxNbKekuleStructuresFilteringCriterion(FilteringPane parent,
			ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (isValid()) {
			NbKekuleStructuresCriterion criterion = new NbKekuleStructuresCriterion(
					FilteringOperator.getOperator(operatorChoiceBox.getValue()),
					Double.parseDouble(fieldValue.getText()));

			criterions.add(criterion);
		}

		return criterions;
	}

}
