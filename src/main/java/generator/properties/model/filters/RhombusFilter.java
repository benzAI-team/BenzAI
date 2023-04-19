package generator.properties.model.filters;

import constraints.RhombusConstraint;

public class RhombusFilter extends DefaultFilter {

	public RhombusFilter() {
		super(new RhombusConstraint());
	}

}
