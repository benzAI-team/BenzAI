package generator.properties.model.checkers;

import generator.properties.model.ModelProperty;
import molecules.Benzenoid;

public abstract class Checker {
	public static final Checker NOCHECKER = new Checker() {
		public boolean checks(Benzenoid molecule, ModelProperty property) { return true;}
	};
	
	public abstract boolean checks(Benzenoid molecule, ModelProperty property);
	
}
