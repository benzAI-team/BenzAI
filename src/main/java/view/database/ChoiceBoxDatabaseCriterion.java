package view.database;

import database.BenzenoidCriterion;
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

		criteria.add(new Couple<>("Id", new HBoxIntDatabaseCriterion(parent, this,BenzenoidCriterion.Subject.ID_MOLECULE,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Label", new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.MOLECULE_LABEL,"=")));
		criteria.add(new Couple<>("InChi", new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.INCHI,"=")));
		criteria.add(new Couple<>("Number of hexagons", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_HEXAGONS,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Number of carbons", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_CARBONS,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Number of hydrogens", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_HYDROGENS,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Irregularity", new HBoxFloatDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.IRREGULARITY,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Frequency", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.FREQUENCY,"IN")));
		criteria.add(new Couple<>("Intensity", new HBoxFloatDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.INTENSITY,"IN")));
		criteria.add(new Couple<>("Solo", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.SOLO,"< <= = != > >= IN")));
		criteria.add(new Couple<>("Duo", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.DUO,"< <= = != > >= IN")));
		criteria.add(new Couple<>("Trio", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.TRIO,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Quartet", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.QUARTET,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Number of Kekulé structures", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.KEKULE,"= < <= != > >= IN")));
		criteria.add(new Couple<>("Catacondensed", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.CATACONDENSED,"=")));
		criteria.add(new Couple<>("Coronenoid", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.CORONENOID,"=")));
		criteria.add(new Couple<>("Coronoid", new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.CORONOID,"=")));
		criteria.add(new Couple<>("Symmetry", new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.SYMMETRY,"= !=")));

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
