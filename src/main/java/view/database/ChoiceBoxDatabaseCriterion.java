package view.database;

import javafx.scene.control.ChoiceBox;
import utils.Couple;
import view.database.boxes.HBoxDatabaseCriterion;
import view.database.boxes.HBoxFloatDatabaseCriterion;
import view.database.boxes.HBoxIntDatabaseCriterion;
import view.database.boxes.HBoxStringDatabaseCriterion;

import java.util.ArrayList;

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
		ArrayList<Couple<String, HBoxDatabaseCriterion>> criteria = new ArrayList<>();

		criteria.add(new Couple<>("Id", new HBoxIntDatabaseCriterion(parent, this, "idBenzenoid","= < <= != > >= IN")));
		criteria.add(new Couple<>("Label", new HBoxStringDatabaseCriterion (parent, this, "label","=")));
		criteria.add(new Couple<>("InChi", new HBoxStringDatabaseCriterion (parent, this, "inchi","=")));
		criteria.add(new Couple<>("# hexagons", new HBoxIntDatabaseCriterion(parent, this, "nbHexagons","= < <= != > >= IN")));
		criteria.add(new Couple<>("# carbons", new HBoxIntDatabaseCriterion(parent, this, "nbCarbons","= < <= != > >= IN")));
		criteria.add(new Couple<>("# hydrogens", new HBoxIntDatabaseCriterion(parent, this, "nbHydrogens","= < <= != > >= IN")));
		criteria.add(new Couple<>("diameter", new HBoxIntDatabaseCriterion(parent, this, "diameter","= < <= != > >= IN")));
		criteria.add(new Couple<>("Irregularity", new HBoxFloatDatabaseCriterion(parent, this, "irregularity","= < <= != > >= IN")));
		criteria.add(new Couple<>("Frequency", new HBoxIntDatabaseCriterion(parent, this, "frequency","IN")));
		criteria.add(new Couple<>("Intensity", new HBoxFloatDatabaseCriterion(parent, this, "intensity","IN")));
		criteria.add(new Couple<>("# solo", new HBoxIntDatabaseCriterion(parent, this, "solo","= < <= != > >= IN")));
		criteria.add(new Couple<>("# duo", new HBoxIntDatabaseCriterion(parent, this, "duo","= < <= != > >= IN")));
		criteria.add(new Couple<>("# trio", new HBoxIntDatabaseCriterion(parent, this, "trio","= < <= != > >= IN")));
		criteria.add(new Couple<>("# quartet", new HBoxIntDatabaseCriterion(parent, this, "quartet","= < <= != > >= IN")));
		criteria.add(new Couple<>("# Kekulé structures", new HBoxIntDatabaseCriterion(parent, this, "kekule","= < <= != > >= IN")));
		criteria.add(new Couple<>("Clar number", new HBoxIntDatabaseCriterion(parent, this, "clarNumber","= < <= != > >= IN")));
		criteria.add(new Couple<>("Is catacondensed", new HBoxIntDatabaseCriterion(parent, this, "catacondensed","=")));
		criteria.add(new Couple<>("Is Coronenoid", new HBoxIntDatabaseCriterion(parent, this, "coronenoid","=")));
		criteria.add(new Couple<>("Is Coronoid", new HBoxIntDatabaseCriterion(parent, this, "coronoid","=")));
		criteria.add(new Couple<>("Is planar", new HBoxIntDatabaseCriterion(parent, this, "planar","=")));
		criteria.add(new Couple<>("Symmetry", new HBoxStringDatabaseCriterion (parent, this, "symmetry","= !=")));
		criteria.add(new Couple<>("# coves ", new HBoxIntDatabaseCriterion(parent, this, "cove","= < <= != > >= IN")));
		criteria.add(new Couple<>("# fjords ", new HBoxIntDatabaseCriterion(parent, this, "fjord","= < <= != > >= IN")));


		for (Couple<String, HBoxDatabaseCriterion> criterion : criteria) {
			this.getItems().add(criterion.getX());
		}

		this.setOnAction(e -> {

			if (getValue() != null) {
				String value = getValue();
				int i = 0;
				while ((i < criteria.size()) && (! criteria.get(i).getX().equals(value))) {
					i++;
				}

				if (i < criteria.size()) {
					parent.setHBox(index, criteria.get(i).getY());
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
