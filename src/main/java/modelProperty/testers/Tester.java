package modelProperty.testers;

import java.util.ArrayList;

import generator.properties.Property;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public abstract class Tester {
	public static Tester NO_TESTER = new Tester () {
		public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList) {
			return true;
		}
	};
	
	public abstract boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList);
	
	static boolean testAll(Molecule molecule, ModelPropertySet modelPropertySet) {
		for(Property modelProperty : modelPropertySet)
			if(!((ModelProperty)modelProperty).getTester().test(molecule, modelProperty.getExpressions()))
				return false;
		return true;
	}
}
