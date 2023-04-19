package generator.properties.model.filters;

import java.util.ArrayList;

import generator.properties.Property;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.PropertyExpression;
import molecules.Molecule;

public abstract class Filter {
	public static Filter NO_Filter = new Filter () {
		@Override
		public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList,
				ModelPropertySet modelPropertySet) {
			return false;
		}
	};
	
	public abstract boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet);
	
	public static boolean testAll(Molecule molecule, ModelPropertySet modelPropertySet) {
		for(Property modelProperty : modelPropertySet)
			if(((ModelProperty)modelProperty).hasExpressions() && !((ModelProperty)modelProperty).getFilter().test(molecule, modelProperty.getExpressions(), modelPropertySet))
				return false;
		return true;
	}
}
