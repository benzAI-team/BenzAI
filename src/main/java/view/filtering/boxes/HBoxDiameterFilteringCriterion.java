package view.filtering.boxes;

import java.util.ArrayList;

import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.DiameterCriterion;
import view.filtering.criterions.FilteringCriterion;

public class HBoxDiameterFilteringCriterion extends HBoxClassicalFilteringCriterion {

	public HBoxDiameterFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {

		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (valid) {
			String operatorDiameter = operatorChoiceBox.getValue();
			int nbHoles = Integer.parseInt(fieldValue.getText());
			criterions.add(new DiameterCriterion(operatorDiameter, nbHoles));
		}

		return criterions;
	}

}
