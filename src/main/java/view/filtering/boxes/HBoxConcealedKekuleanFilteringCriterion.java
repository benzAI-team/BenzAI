package view.filtering.boxes;

import java.util.ArrayList;

import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.ConcealedNonKekuleanCriterion;
import view.filtering.criterions.FilteringCriterion;

public class HBoxConcealedKekuleanFilteringCriterion extends HBoxFilteringCriterion {

	public HBoxConcealedKekuleanFilteringCriterion(FilteringPane parent,
			ChoiceBoxFilteringCriterion choiceBoxCriterion) {
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
		criterions.add(new ConcealedNonKekuleanCriterion());
		return criterions;
	}

}
