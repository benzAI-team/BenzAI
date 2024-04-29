package view.generator;

import javafx.scene.control.ChoiceBox;
import properties.PropertySet;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDefaultCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class ChoiceBoxCriterion extends ChoiceBox<String> {

	private int index;
	private final ScrollPaneWithPropertyList parent;
	private HBoxCriterion hBoxCriterion;


	public ChoiceBoxCriterion(int index, ScrollPaneWithPropertyList parent, PropertySet modelPropertySet) {
		super();
		this.index = index;
		this.parent = parent;
		initialize(modelPropertySet);
	}

	private void initialize(PropertySet modelPropertySet) {
		this.getItems().addAll(modelPropertySet.getNames());
		sethBoxCriterion(new HBoxDefaultCriterion(parent, this));
		this.setOnAction(e -> {
			if (getValue() != null) {
				String value = getValue();
				hBoxCriterion = modelPropertySet.makeHBoxCriterion(parent, this, value);
				parent.setHBox(index, hBoxCriterion);
			}
		});
	}

	/** getters, setters
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public HBoxCriterion getHBoxCriterion() {
		return hBoxCriterion;
	}

	public void sethBoxCriterion(HBoxCriterion hBoxCriterion) {
		this.hBoxCriterion = hBoxCriterion;
	}

	@Override
	public String toString() {
		return "ChoiceBoxCriterion::" + index;
	}
}
