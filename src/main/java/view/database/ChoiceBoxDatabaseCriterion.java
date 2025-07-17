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
		criteria.add(new Couple<>("Number of hexagons", new HBoxIntDatabaseCriterion(parent, this, "nbHexagons","= < <= != > >= IN")));
		criteria.add(new Couple<>("Number of carbons", new HBoxIntDatabaseCriterion(parent, this, "nbCarbons","= < <= != > >= IN")));
		criteria.add(new Couple<>("Number of hydrogens", new HBoxIntDatabaseCriterion(parent, this, "nbHydrogens","= < <= != > >= IN")));
		criteria.add(new Couple<>("Irregularity", new HBoxFloatDatabaseCriterion(parent, this, "irregularity","= < <= != > >= IN")));
		criteria.add(new Couple<>("Frequency", new HBoxIntDatabaseCriterion(parent, this, "frequency","IN")));
		criteria.add(new Couple<>("Intensity", new HBoxFloatDatabaseCriterion(parent, this, "intensity","IN")));
		criteria.add(new Couple<>("Solo", new HBoxIntDatabaseCriterion(parent, this, "solo","< <= = != > >= IN")));
		criteria.add(new Couple<>("Duo", new HBoxIntDatabaseCriterion(parent, this, "duo","< <= = != > >= IN")));
		criteria.add(new Couple<>("Trio", new HBoxIntDatabaseCriterion(parent, this, "trio","= < <= != > >= IN")));
		criteria.add(new Couple<>("Quartet", new HBoxIntDatabaseCriterion(parent, this, "quartet","= < <= != > >= IN")));
		criteria.add(new Couple<>("Number of Kekulé structures", new HBoxIntDatabaseCriterion(parent, this, "kekule","= < <= != > >= IN")));
		criteria.add(new Couple<>("Catacondensed", new HBoxIntDatabaseCriterion(parent, this, "catacondensed","=")));
		criteria.add(new Couple<>("Coronenoid", new HBoxIntDatabaseCriterion(parent, this, "coronenoid","=")));
		criteria.add(new Couple<>("Coronoid", new HBoxIntDatabaseCriterion(parent, this, "coronoid","=")));
		criteria.add(new Couple<>("Symmetry", new HBoxStringDatabaseCriterion (parent, this, "symmetry","= !=")));


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
