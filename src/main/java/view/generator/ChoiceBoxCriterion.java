package view.generator;

import javafx.scene.control.ChoiceBox;
import utils.Utils;
import view.generator.boxes.HBoxCatacondensedCriterion;
import view.generator.boxes.HBoxConcealedCriterion;
import view.generator.boxes.HBoxCoronenoidCriterion;
import view.generator.boxes.HBoxCoronoidCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDiameterCriterion;
import view.generator.boxes.HBoxIrregularityCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;
import view.generator.boxes.HBoxNbHexagonsCriterion;
import view.generator.boxes.HBoxNbHydrogensCriterion;
import view.generator.boxes.HBoxNbKekuleStructuresCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;
import view.generator.boxes.HBoxPatternCriterion;
import view.generator.boxes.HBoxRectangleCriterion;
import view.generator.boxes.HBoxRhombusCriterion;
import view.generator.boxes.HBoxSymmetriesCriterion;
import view.generator.boxes.HBoxTimeoutCriterion;

public class ChoiceBoxCriterion extends ChoiceBox<String> {

	private int index;
	private GeneratorPane parent;

	public ChoiceBoxCriterion(int index, GeneratorPane parent) {
		super();
		this.index = index;
		this.parent = parent;
		initialize();
	}

	private void initialize() {

		this.getItems().add("Number of hexagons");
		this.getItems().add("Number of carbons");
		this.getItems().add("Number of hydrogens");
		this.getItems().add("Coronenoid");
		this.getItems().add("Irregularity");
		this.getItems().add("Diameter");
		this.getItems().add("Coronoid");
		this.getItems().add("Rectangle");
		this.getItems().add("Rhombus");
		this.getItems().add("Catacondensed");
		this.getItems().add("Symmetries");
		this.getItems().add("Pattern properties");
		this.getItems().add("Number of Kekule structures");
		this.getItems().add("Concealed non kekulean");
		this.getItems().add("Number of solutions");
		this.getItems().add("Time limit");

		this.setOnAction(e -> {

			if (getValue() != null) {

				String value = getValue();

				System.out.println(value);

				if (value.equals("Number of hexagons")) {
					HBoxCriterion box = new HBoxNbHexagonsCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of carbons")) {
					HBoxCriterion box = new HBoxNbCarbonsCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of hydrogens")) {
					HBoxCriterion box = new HBoxNbHydrogensCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Coronenoid")) {
					HBoxCriterion box = new HBoxCoronenoidCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Diameter")) {
					HBoxCriterion box = new HBoxDiameterCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Irregularity")) {
					HBoxCriterion box = new HBoxIrregularityCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Coronoid")) {
					HBoxCriterion box = new HBoxCoronoidCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Rectangle")) {
					HBoxCriterion box = new HBoxRectangleCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Rhombus")) {
					HBoxCriterion box = new HBoxRhombusCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Catacondensed")) {
					HBoxCriterion box = new HBoxCatacondensedCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Symmetries")) {

					boolean existing = false;

					for (HBoxCriterion box : parent.getHBoxesCriterions()) {
						if (box instanceof HBoxSymmetriesCriterion)
							existing = true;
					}

					if (!existing) {
						HBoxCriterion box = new HBoxSymmetriesCriterion(parent, this);
						parent.setHBox(index, box);
					}

					else {
						Utils.alert("Only one symmetry criterion");
					}
				}

				else if (value.equals("Pattern properties")) {
					HBoxCriterion box = new HBoxPatternCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of solutions")) {
					HBoxCriterion box = new HBoxNbSolutionsCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of Kekulé structures")) {
					HBoxCriterion box = new HBoxNbKekuleStructuresCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Concealed non kekulean")) {
					HBoxCriterion box = new HBoxConcealedCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Time limit")) {
					HBoxCriterion box = new HBoxTimeoutCriterion(parent, this);
					parent.setHBox(index, box);
				}
			}
		});
	}

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
