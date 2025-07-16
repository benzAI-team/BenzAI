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
		this.getItems().add("Solo");
		this.getItems().add("Duo");
		this.getItems().add("Trio");
		this.getItems().add("Quartet");
		this.getItems().add("Number of Kekulé structures");
		this.getItems().add("Catacondensed");
		this.getItems().add("Coronenoid");
		this.getItems().add("Coronoid");
		this.getItems().add("Symmetry");
		this.getItems().add("Clar number");

		this.setOnAction(e -> {

			if (getValue() != null) {

				String value = getValue();
   			    HBoxDatabaseCriterion box  = null;
				if ("Id".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this,BenzenoidCriterion.Subject.ID_MOLECULE,"= < <= != > >= IN");
				}

				else if ("Label".equals(value)) {
					box = new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.MOLECULE_LABEL,"=");
				}
        
				else if ("InChi".equals(value)) {
					box = new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.INCHI,"=");
				}

				else if ("Number of hexagons".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_HEXAGONS,"= < <= != > >= IN");
				}

				else if ("Number of carbons".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_CARBONS,"= < <= != > >= IN");
				}

				else if ("Number of hydrogens".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.NB_HYDROGENS,"= < <= != > >= IN");
				}

				else if ("Irregularity".equals(value)) {
					box = new HBoxFloatDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.IRREGULARITY,"= < <= != > >= IN");
				}

				else if ("Frequency".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.FREQUENCY,"IN");
				}

				else if ("Intensity".equals(value)) {
					box = new HBoxFloatDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.INTENSITY,"IN");
				}

				else if ("Solo".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.SOLO,"< <= = != > >= IN");
				}
        
				else if ("Duo".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.DUO,"< <= = != > >= IN");
				}
        
				else if ("Trio".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.TRIO,"= < <= != > >= IN");
				}
        
				else if ("Quartet".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.QUARTET,"= < <= != > >= IN");
				}
        
				else if ("Number of Kekulé structures".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.KEKULE,"= < <= != > >= IN");
				}

				else if ("Catacondensed".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.CATACONDENSED,"=");
				}
        
				else if ("Coronenoid".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.CORONENOID,"=");
				}
        
				else if ("Coronoid".equals(value)) {
					box = new HBoxIntDatabaseCriterion(parent, this, BenzenoidCriterion.Subject.CORONOID,"=");
				}
        
        		else if ("Symmetry".equals(value)) {
					box = new HBoxStringDatabaseCriterion (parent, this, BenzenoidCriterion.Subject.SYMMETRY,"= !=");
				}

				else {

				}
        if (box != null) {
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
