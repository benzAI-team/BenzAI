package generator.properties.model.filters;

import constraints.DiameterConstraint;

public class DiameterFilter extends DefaultFilter {

	public DiameterFilter() {
		super(new DiameterConstraint());
	}

}
