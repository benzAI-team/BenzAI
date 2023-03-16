package modelProperty.testers;

import java.util.ArrayList;

import generator.properties.Property;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public abstract class Tester {
	public static Tester NO_TESTER = new Tester () {
		@Override
		public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList,
				ModelPropertySet modelPropertySet) {
			return false;
		}
	};
	
	public abstract boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet);
	
	public static boolean testAll(Molecule molecule, ModelPropertySet modelPropertySet) {
		for(Property modelProperty : modelPropertySet)
			if(((ModelProperty)modelProperty).hasExpressions() && !((ModelProperty)modelProperty).getTester().test(molecule, modelProperty.getExpressions(), modelPropertySet))
				return false;
		return true;
	}
}
