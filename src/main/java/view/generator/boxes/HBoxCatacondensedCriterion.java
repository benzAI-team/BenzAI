package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxCatacondensedCriterion extends HBoxCriterion {

	public HBoxCatacondensedCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		valid = true;
	}

	@Override
	protected void checkValidity() {
		valid = true;
		parent.refreshGenerationPossibility();
	}

	@Override
	protected void initialize() {
		this.getChildren().add(deleteButton);
	}

	@Override
	public ArrayList<GeneratorCriterion> buildCriterions() {
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		criterions.add(new GeneratorCriterion(Subject.CATACONDENSED, Operator.NONE, ""));
		return criterions;
	}

}
