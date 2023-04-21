package generator.properties.model.checkers;

import generator.properties.model.ModelProperty;
import molecules.Molecule;

public abstract class Checker {
	public static final Checker NOCHECKER = new Checker() {
		public boolean checks(Molecule molecule, ModelProperty property) { return true;}
	};
	
	public abstract boolean checks(Molecule molecule, ModelProperty property);	
	
}
