package view.filtering.boxes;

import java.util.ArrayList;

import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.AromaticityFilteringCriterion;
import view.filtering.criterions.FilteringCriterion;

public class HBoxAromaticityFilteringCriterion extends HBoxFilteringCriterion {

	public HBoxAromaticityFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		valid = true;

	}

	@Override
	protected void initialize() {
		checkValidity();
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();
		criterions.add(new AromaticityFilteringCriterion());
		return criterions;
	}

}
