package view.generator.boxes;


import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import generator.properties.model.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class HBoxCriterion extends HBox {
	private boolean valid;

	private boolean bounding;

	private ImageView warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
	private final DeleteButton deleteButton = new DeleteButton();

	private ScrollPaneWithPropertyList pane;

	private PropertyExpression expression;

	
	public HBoxCriterion(ScrollPaneWithPropertyList pane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(5.0);
		this.setPane(pane);
		Tooltip.install(warningIcon, new Tooltip("Invalid entry, criterion will not be considered"));
			initialize();
	}

	public abstract void updateValidity();

	protected abstract void initialize();

	/***
	 * getters, setters
	 */

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

	protected void removeWarningIcon() {
		this.getChildren().remove(getWarningIcon());
	}

	protected void addWarningIcon() {
		this.getChildren().addAll(getWarningIcon());
	}

	public abstract void assign(PropertyExpression expression);

	public abstract void initEventHandling();

	public boolean isBounding() {
		return bounding;
	}

	public void setBounding(boolean bounding) {
		this.bounding = bounding;
	}

	public DeleteButton getDeleteButton() {
		return deleteButton;
	}
}
