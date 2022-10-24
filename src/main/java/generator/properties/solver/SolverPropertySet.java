package generator.properties.solver;

import java.util.ArrayList;

import generator.properties.Property;
import generator.properties.PropertySet;
import modelProperty.ModelProperty;

public class SolverPropertySet extends PropertySet<SolverProperty> {

	public SolverPropertySet(){
		setPropertyList(new ArrayList<SolverProperty>());
		add(new TimeLimitProperty());
		add(new SolutionNumberProperty());

	}

	public boolean has(String id) {
		SolverProperty property =  getById(id);
		return property != null && property.getExpression() != null;
	}

}
