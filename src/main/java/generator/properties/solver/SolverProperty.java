package generator.properties.solver;

import generator.properties.Property;

public abstract class SolverProperty extends Property{
	private SolverSpecifier specifier;
	
	public SolverProperty(String id, String name, SolverSpecifier specifier) {
		super(id, name);
		this.specifier = specifier;
	}
	
	public SolverSpecifier getSpecifier() {
		return specifier;
	}

	public void setSpecifier(SolverSpecifier specifier) {
		this.specifier = specifier;
	}
	
}
