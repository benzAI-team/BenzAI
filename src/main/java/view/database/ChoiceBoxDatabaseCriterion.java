package view.database;

import javafx.scene.control.ChoiceBox;
import view.database.boxes.HBoxDatabaseCriterion;
import view.database.boxes.HBoxFrequencyDatabaseCriterion;
import view.database.boxes.HBoxIDDatabaseCriterion;
import view.database.boxes.HBoxIntensityDatabaseCriterion;
import view.database.boxes.HBoxIrregularityDatabaseCriterion;
import view.database.boxes.HBoxNbCarbonsDatabaseCriterion;
import view.database.boxes.HBoxNbHexagonsDatabaseCriterion;
import view.database.boxes.HBoxNbHydrogensDatabaseCriterion;

public class ChoiceBoxDatabaseCriterion extends ChoiceBox<String> {

	private int index;
	private DatabasePane parent;

	public ChoiceBoxDatabaseCriterion(int index, DatabasePane parent) {
		super();
		this.index = index;
		this.parent = parent;
		initialize();
	}

	private void initialize() {
		this.getItems().add("Id");
		this.getItems().add("Name");
		this.getItems().add("Number of hexagons");
		this.getItems().add("Number of carbons");
		this.getItems().add("Number of hydrogens");
		this.getItems().add("Irregularity");
		this.getItems().add("Frequency");
		this.getItems().add("Intensity");

		this.setOnAction(e -> {

			if (getValue() != null) {

				String value = getValue();

				if (value.equals("Id")) {
					HBoxDatabaseCriterion box = new HBoxIDDatabaseCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Name")) {
					// HBoxDatabaseCriterion box = new HBoxNameDatabaseCriterion(parent, this);
					// parent.setHBox(index, box);
				}

				else if (value.equals("Number of hexagons")) {
					HBoxDatabaseCriterion box = new HBoxNbHexagonsDatabaseCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of carbons")) {
					HBoxDatabaseCriterion box = new HBoxNbCarbonsDatabaseCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Number of hydrogens")) {
					HBoxDatabaseCriterion box = new HBoxNbHydrogensDatabaseCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Irregularity")) {
					HBoxDatabaseCriterion box = new HBoxIrregularityDatabaseCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Frequency")) {
					HBoxDatabaseCriterion box = new HBoxFrequencyDatabaseCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else if (value.equals("Intensity")) {
					HBoxDatabaseCriterion box = new HBoxIntensityDatabaseCriterion(parent, this);
					parent.setHBox(index, box);
				}

				else {

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
