package view.filtering.boxes;

import java.util.ArrayList;

import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;

public class HBoxDefaultFilteringCriterion extends HBoxFilteringCriterion {

	public HBoxDefaultFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		valid = false;
	}

	@Override
	protected void initialize() {
		this.getChildren().addAll(warningIcon, deleteButton);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		return null;
	}

}
