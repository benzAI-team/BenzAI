package view.generator.boxes;


import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import properties.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class HBoxCriterion extends HBox {
	private boolean valid;

	private boolean bounding;
	
	private final DeleteButton deleteButton = new DeleteButton();
	private ImageView warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
	 
	private ScrollPaneWithPropertyList pane;

	private PropertyExpression expression;

	
	public HBoxCriterion(ScrollPaneWithPropertyList pane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(5.0);
		this.setPane(pane);
		Tooltip.install(warningIcon, new Tooltip("Invalid entry, criterion will not be considered"));
		Tooltip.install(deleteButton, new Tooltip("Delete criterion"));
		deleteButton.setOnAction(e -> pane.removeCriterion(choiceBoxCriterion, this));
		initialize();
	}

	public abstract void updateValidity();

	protected void addDeleteButton() {
		this.getChildren().add(getDeleteButton());
	}

	protected abstract void initialize();

	/***
	 * getters, setters
	 */
	public DeleteButton getDeleteButton() {
		return deleteButton;
	}

	public ImageView getWarningIcon() {
		return warningIcon;
	}

	void setWarningIcon(ImageView warningIcon) {
		this.warningIcon = warningIcon;
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

	protected void removeWarningIconAndDeleteButton() {
		this.getChildren().remove(getWarningIcon());
		this.getChildren().remove(getDeleteButton());
	}

	protected void addWarningIconAndDeleteButton() {
		this.getChildren().addAll(getWarningIcon(), getDeleteButton());
	}

	public abstract void assign(PropertyExpression expression);

	public abstract void initEventHandling();

	public boolean isBounding() {
		return bounding;
	}

	public void setBounding(boolean bounding) {
		this.bounding = bounding;
	}

}
