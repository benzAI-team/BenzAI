package view.generator.boxes;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import modelProperty.ModelPropertySet;
import modelProperty.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

@SuppressWarnings("unused")
public abstract class HBoxCriterion extends HBox {

	private boolean valid;
	
	protected DeleteButton deleteButton;
	private ImageView warningIcon;
	 
	private GeneratorPane generatorPane;
	private ChoiceBoxCriterion choiceBoxCriterion;
	
	public HBoxCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(5.0);
		
		this.generatorPane = generatorPane;
		this.choiceBoxCriterion = choiceBoxCriterion;
		
		warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		deleteButton = new DeleteButton(this);
		
		Tooltip.install(warningIcon, new Tooltip("Invalid entry, criterion will not be considered"));
		Tooltip.install(deleteButton, new Tooltip("Delete criterion"));
		
		deleteButton.setOnAction(e -> {
			generatorPane.removeCriterion(choiceBoxCriterion, this);
		});
		
		initialize();
	}
	
	protected abstract void checkValidity();
	protected abstract void initialize();
	
	public abstract void addPropertyExpression(ModelPropertySet modelPropertySet);
	

	/***
	 * getters, setters
	 */
	public DeleteButton getDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(DeleteButton deleteButton) {
		this.deleteButton = deleteButton;
	}

	public ImageView getWarningIcon() {
		return warningIcon;
	}

	public void setWarningIcon(ImageView warningIcon) {
		this.warningIcon = warningIcon;
	}


	public GeneratorPane getGeneratorPane() {
		return generatorPane;
	}

	public void setGeneratorPane(GeneratorPane generatorPane) {
		this.generatorPane = generatorPane;
	}

	public ChoiceBoxCriterion getChoiceBoxCriterion() {
		return choiceBoxCriterion;
	}

	public void setChoiceBoxCriterion(ChoiceBoxCriterion choiceBoxCriterion) {
		this.choiceBoxCriterion = choiceBoxCriterion;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	

}
