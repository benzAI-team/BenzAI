package view.filtering.boxes;

import java.util.ArrayList;

import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.FilteringOperator;
import view.filtering.criterions.NbHexagonsCriterion;

public class HBoxNbHexagonsFilteringCriterion extends HBoxClassicalFilteringCriterion {

	public HBoxNbHexagonsFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (isValid()) {
			NbHexagonsCriterion criterion = new NbHexagonsCriterion(
					FilteringOperator.getOperator(operatorChoiceBox.getValue()),
					Integer.parseInt(fieldValue.getText()));

			criterions.add(criterion);
		}

		return criterions;
	}

}
