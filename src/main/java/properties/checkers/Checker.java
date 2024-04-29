package properties.checkers;

import properties.ModelProperty;
import benzenoid.Benzenoid;

public abstract class Checker {
	public static final Checker NOCHECKER = new Checker() {
		public boolean checks(Benzenoid molecule, ModelProperty property) { return true;}
	};
	
	public abstract boolean checks(Benzenoid molecule, ModelProperty property);
	
}
