package view.generator;

import generator.properties.solver.SolverPropertySet;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;
import modelProperty.ModelPropertySet;

public class ChoiceBoxCriterion extends ChoiceBox<String> {

	private int index;
	private ScrollPaneWithPropertyList parent;

	
	public ChoiceBoxCriterion(int index, ScrollPaneWithPropertyList parent, ModelPropertySet modelPropertySet) {
		super();
		this.index = index;
		this.parent = parent;
		initialize(modelPropertySet);
	}

	private void initialize(ModelPropertySet modelPropertySet) {
		this.getItems().addAll(modelPropertySet.getNames());
				
		this.setOnAction(e -> {
			if (getValue() != null) {
				String value = getValue();
				System.out.println(value);
				HBoxCriterion box = modelPropertySet.getHBoxCriterion(parent, this, value);
				parent.setHBox(index, box);
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


	@Override
	public String toString() {
		return "ChoiceBoxCriterion::" + index;
	}
}
