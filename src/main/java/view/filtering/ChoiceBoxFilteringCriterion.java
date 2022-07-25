package view.filtering;

import javafx.scene.control.ChoiceBox;
import view.filtering.boxes.HBoxAromaticityFilteringCriterion;
import view.filtering.boxes.HBoxCatacondensedFilteringCriterion;
import view.filtering.boxes.HBoxConcealedKekuleanFilteringCriterion;
import view.filtering.boxes.HBoxCoronenoidFilteringCriterion;
import view.filtering.boxes.HBoxCoronoidFilteringCriterion;
import view.filtering.boxes.HBoxDiameterFilteringCriterion;
import view.filtering.boxes.HBoxFilteringCriterion;
import view.filtering.boxes.HBoxIrregularityFilteringCriterion;
import view.filtering.boxes.HBoxNbCarbonsFilteringCriterion;
import view.filtering.boxes.HBoxNbHexagonsFilteringCriterion;
import view.filtering.boxes.HBoxNbHydrogensFilteringCriterion;
import view.filtering.boxes.HBoxNbKekuleStructuresFilteringCriterion;
import view.filtering.boxes.HBoxPatternFilteringCriterion;
import view.filtering.boxes.HBoxRectangleFilteringCriterion;
import view.filtering.boxes.HBoxRhombusFilteringCriterion;
import view.filtering.boxes.HBoxSymmetriesFilteringCriterion;

public class ChoiceBoxFilteringCriterion extends ChoiceBox<String> {

	private int index;
	private FilteringPane parent;

	public ChoiceBoxFilteringCriterion(int index, FilteringPane parent) {
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
		this.getItems().add("Concealed Kekulean");
		this.getItems().add("Fully aromatic");

		this.setOnAction(e -> {

			if (getValue() != null) {

				String value = getValue();

				if (value.equals("Number of hexagons")) {
					HBoxFilteringCriterion box = new HBoxNbHexagonsFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of carbons")) {
					HBoxFilteringCriterion box = new HBoxNbCarbonsFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of hydrogens")) {
					HBoxFilteringCriterion box = new HBoxNbHydrogensFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Coronenoid")) {
					HBoxFilteringCriterion box = new HBoxCoronenoidFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of Kekule structures")) {
					HBoxFilteringCriterion box = new HBoxNbKekuleStructuresFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Concealed Kekulean")) {
					HBoxFilteringCriterion box = new HBoxConcealedKekuleanFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Irregularity")) {
					HBoxFilteringCriterion box = new HBoxIrregularityFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Rectangle")) {
					HBoxFilteringCriterion box = new HBoxRectangleFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Catacondensed")) {
					HBoxFilteringCriterion box = new HBoxCatacondensedFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Coronoid")) {
					HBoxFilteringCriterion box = new HBoxCoronoidFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Pattern properties")) {
					HBoxFilteringCriterion box = new HBoxPatternFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Diameter")) {
					HBoxFilteringCriterion box = new HBoxDiameterFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Rhombus")) {
					HBoxFilteringCriterion box = new HBoxRhombusFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Symmetries")) {
					HBoxFilteringCriterion box = new HBoxSymmetriesFilteringCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Fully aromatic")) {
					HBoxFilteringCriterion box = new HBoxAromaticityFilteringCriterion(parent, this);
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
		return "ChoiceBoxFilteringCriterion::" + index;
	}
}
