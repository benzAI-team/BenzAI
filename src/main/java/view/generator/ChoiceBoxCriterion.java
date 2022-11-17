package view.generator;

import generator.properties.solver.SolverPropertySet;
import javafx.scene.control.ChoiceBox;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import modelProperty.ModelPropertySet;

public class ChoiceBoxCriterion extends ChoiceBox<String> {

	private int index;
	private GeneratorPane parent;
	private ModelPropertySet modelPropertySet;
	private SolverPropertySet solverPropertySet;

	
	public ChoiceBoxCriterion(int index, GeneratorPane parent, ModelPropertySet modelPropertySet, SolverPropertySet solverPropertySet) {
		super();
		this.index = index;
		this.parent = parent;
		this.modelPropertySet = modelPropertySet;
		this.solverPropertySet = solverPropertySet;
		initialize();
	}

	private void initialize() {
		for(String name : modelPropertySet.getNames())
			this.getItems().add(name);				
				
		this.setOnAction(e -> {

			if (getValue() != null) {

				String value = getValue();

				System.out.println(value);
				//TODO pas forcément le modelProperSet, le solverPropertySet aussi 
				HBoxCriterion box = modelPropertySet.getHBoxCriterion(parent, this, value);
				parent.setHBox(index, box);
//				else if (value.equals("Symmetries")) {
//
//					boolean existing = false;
//
//					for (HBoxCriterion box : parent.getHBoxesCriterions()) {
//						if (box instanceof HBoxSymmetriesCriterion)
//							existing = true;
//					}
//
//					if (!existing) {
//						HBoxCriterion box = new HBoxSymmetriesCriterion(parent, this);
//						parent.setHBox(index, box);
//					}
//
//					else {
//						Utils.alert("Only one symmetry criterion");
//					}
//				}
//
//				else if (value.equals("Number of solutions")) {
//					HBoxCriterion box = new HBoxNbSolutionsCriterion(parent, this);
//					parent.setHBox(index, box);
//				}
//
//				else if (value.equals("Number of Kekulé structures")) {
//					HBoxCriterion box = new HBoxNbKekuleStructuresCriterion(parent, this);
//					parent.setHBox(index, box);
//				}
//
//				else if (value.equals("Concealed non Kekulean")) {
//					HBoxCriterion box = new HBoxConcealedCriterion(parent, this);
//					parent.setHBox(index, box);
//				}
//
//				else if (value.equals("Time limit")) {
//					HBoxCriterion box = new HBoxTimeoutCriterion(parent, this);
//					parent.setHBox(index, box);
//				}
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
