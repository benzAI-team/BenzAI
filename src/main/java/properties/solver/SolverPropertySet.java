package properties.solver;

import java.util.ArrayList;

import properties.PropertySet;

public class SolverPropertySet extends PropertySet {
	public SolverPropertySet(){
		setPropertyList(new ArrayList<>());
		add(new TimeLimitProperty());
		add(new SolutionNumberProperty());

	}
}
