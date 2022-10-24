package modelProperty.checkers;

import java.util.ArrayList;

import modelProperty.ModelProperty;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public abstract class Checker {
	public static Checker NOCHECKER = new Checker() {
		public boolean checks(Molecule molecule, ModelProperty property) { return true;}
	};
	
	public abstract boolean checks(Molecule molecule, ModelProperty property);	
	
}
