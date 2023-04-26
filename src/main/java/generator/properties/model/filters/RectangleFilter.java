package generator.properties.model.filters;

import constraints.RectangleConstraint;

public class RectangleFilter extends DefaultFilter {

	public RectangleFilter() {
		super(new RectangleConstraint());
	}

}
