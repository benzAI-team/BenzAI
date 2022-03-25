package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxDefaultCriterion extends HBoxCriterion {

	public HBoxDefaultCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
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
	public ArrayList<GeneratorCriterion> buildCriterions() {
		return null;
	}

	
}
