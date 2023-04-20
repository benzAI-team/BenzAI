package generator.properties.model.filters;

import constraints.CoronoidConstraint2;

public class CoronoidFilter extends DefaultFilter {

	public CoronoidFilter() {
		super(new CoronoidConstraint2());
	}

}
