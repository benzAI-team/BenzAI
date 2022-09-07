package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import javafx.scene.control.TextField;
import modelProperty.ModelPropertySet;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxNbSolutionsCriterion extends HBoxCriterion {

	private TextField nbSolutionsField;

	public HBoxNbSolutionsCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		if (!Utils.isNumber(nbSolutionsField.getText())) {
			valid = false;
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().addAll(getWarningIcon(), getDeleteButton());
		}

		else {
			valid = true;
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().add(getDeleteButton());
		}
	}

	@Override
	protected void initialize() {

		valid = false;

		nbSolutionsField = new TextField();
		nbSolutionsField.setOnKeyReleased(e -> {
			checkValidity();
		});

		this.getChildren().addAll(nbSolutionsField, getWarningIcon(), getDeleteButton());
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		if (valid) {
			Subject subject = Subject.NB_SOLUTIONS;
			Operator operator = Operator.EQ;
			String value = nbSolutionsField.getText();
			criterions.add(new GeneratorCriterion(subject, operator, value));
		}

		return criterions;
	}

	public void setNbSolutions(String nbSolutions) {
		nbSolutionsField.setText(nbSolutions);
		checkValidity();
	}
}
