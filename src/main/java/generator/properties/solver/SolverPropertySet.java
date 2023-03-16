package generator.properties.solver;

import java.util.ArrayList;

import generator.properties.Property;
import generator.properties.PropertySet;
import modelProperty.ModelProperty;

public class SolverPropertySet extends PropertySet {

	public SolverPropertySet(){
		setPropertyList(new ArrayList<Property>());
		add(new TimeLimitProperty());
		add(new SolutionNumberProperty());

	}
	
	public void clearPropertyExpressions() {
		for(Property property : getPropertyList())
			property.clearExpressions();
	}
}
