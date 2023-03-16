package view.generator.boxes;


import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import modelProperty.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class HBoxCriterion extends HBox {

	private boolean valid;
	
	private DeleteButton deleteButton;
	private ImageView warningIcon;
	 
	private ScrollPaneWithPropertyList pane;
	private ChoiceBoxCriterion choiceBoxCriterion;
	
	private PropertyExpression expression;

	
	public HBoxCriterion(ScrollPaneWithPropertyList pane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(5.0);
		
		this.setPane(pane);
		this.choiceBoxCriterion = choiceBoxCriterion;
		
		warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		deleteButton = new DeleteButton(this);
		
		Tooltip.install(warningIcon, new Tooltip("Invalid entry, criterion will not be considered"));
		Tooltip.install(deleteButton, new Tooltip("Delete criterion"));
		
		deleteButton.setOnAction(e -> {
			pane.removeCriterion(choiceBoxCriterion, this);
		});
		
		initialize();
	}
	
	protected abstract void checkValidity();
	protected abstract void initialize();
	
	//public abstract void addPropertyExpression(PropertySet<Property> propertySet);
	

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



	public ChoiceBoxCriterion getChoiceBoxCriterion() {
		return choiceBoxCriterion;
	}

	public void setChoiceBoxCriterion(ChoiceBoxCriterion choiceBoxCriterion) {
		this.choiceBoxCriterion = choiceBoxCriterion;
	}

	public PropertyExpression getExpression() {
		return expression;
	}


	public void setExpression(PropertyExpression expression) {
		this.expression = expression;
	}


	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public ScrollPaneWithPropertyList getPane() {
		return pane;
	}

	public void setPane(ScrollPaneWithPropertyList pane) {
		this.pane = pane;
	}
	

}
