package generator.properties.model.filters;

import java.util.ArrayList;

import generator.properties.Property;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.PropertyExpression;
import benzenoid.Benzenoid;

public abstract class Filter {

	public abstract boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet);
	
	public static boolean testAll(Benzenoid molecule, ModelPropertySet modelPropertySet) {
		for(Property modelProperty : modelPropertySet)
			if(modelProperty.hasExpressions() && !((ModelProperty)modelProperty).getFilter().test(molecule, modelProperty.getExpressions(), modelPropertySet))
				return false;
		return true;
	}
}
