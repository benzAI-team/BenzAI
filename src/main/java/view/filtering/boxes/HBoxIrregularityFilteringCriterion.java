package view.filtering.boxes;

import java.util.ArrayList;

import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.FilteringOperator;
import view.filtering.criterions.IrregularityCriterion;

public class HBoxIrregularityFilteringCriterion extends HBoxClassicalFilteringCriterion {

	public HBoxIrregularityFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (isValid()) {
			IrregularityCriterion criterion = new IrregularityCriterion(
					FilteringOperator.getOperator(operatorChoiceBox.getValue()),
					Double.parseDouble(fieldValue.getText()));

			criterions.add(criterion);
		}

		return criterions;
	}

}
