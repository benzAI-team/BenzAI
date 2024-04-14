package view.database;

import javafx.scene.control.ChoiceBox;
import database.BenzenoidCriterion;
import view.database.boxes.HBoxDatabaseCriterion;
import view.database.boxes.HBoxIntDatabaseCriterion;
import view.database.boxes.HBoxFloatDatabaseCriterion;
import view.database.boxes.HBoxStringDatabaseCriterion;

public class ChoiceBoxDatabaseCriterion extends ChoiceBox<String> {

	private int index;
	private final DatabasePane parent;

	public ChoiceBoxDatabaseCriterion(int index, DatabasePane parent) {
		super();
		this.index = index;
		this.parent = parent;
		initialize();
	}

	private void initialize() {
		this.getItems().add("Id");
		this.getItems().add("Label");
		this.getItems().add("InChi");
		this.getItems().add("Number of hexagons");
		this.getItems().add("Number of carbons");
		this.getItems().add("Number of hydrogens");
		this.getItems().add("Irregularity");
		this.getItems().add("Frequency");
		this.getItems().add("Intensity");

		this.setOnAction(e -> {

			if (getValue() != null) {

				String value = getValue();

				if ("Id".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxIntDatabaseCriterion(parent, this,BenzenoidCriterion.Subject.ID_MOLECULE,"< <= = != > >= IN");
					parent.setHBox(index, box);
				}

				else if ("Label".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.MOLECULE_LABEL,"=");
					parent.setHBox(index, box);
				}
				else if ("InChi".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.INCHI,"=");
					parent.setHBox(index, box);
				}

				else if ("Number of hexagons".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_HEXAGONS,"< <= = != > >= IN");
					parent.setHBox(index, box);
				}

				else if ("Number of carbons".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_CARBONS,"< <= = != > >= IN");
					parent.setHBox(index, box);
				}

				else if ("Number of hydrogens".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_HYDROGENS,"< <= = != > >= IN");
					parent.setHBox(index, box);
				}

				else if ("Irregularity".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxFloatDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.IRREGULARITY,"< <= = != > >= IN");
					parent.setHBox(index, box);
				}

				else if ("Frequency".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.FREQUENCY,"< <= = != > >= IN");
					parent.setHBox(index, box);
				}

				else if ("Intensity".equals(value)) {
					HBoxDatabaseCriterion box = new HBoxFloatDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.INTENSITY,"< <= > >= IN");
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
