package properties.filters;

import java.util.ArrayList;

import properties.Property;
import properties.ModelProperty;
import properties.ModelPropertySet;
import properties.PropertySet;
import properties.expression.PropertyExpression;
import benzenoid.Benzenoid;

public abstract class Filter {

	public abstract boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet);
	
	public static boolean testAll(Benzenoid molecule, PropertySet modelPropertySet) {
		for(Property modelProperty : modelPropertySet)
			if(modelProperty.hasExpressions() && !((ModelProperty)modelProperty).getFilter().test(molecule, ((ModelProperty)modelProperty).getExpressions(), (ModelPropertySet)modelPropertySet))
				return false;
		return true;
	}
}
