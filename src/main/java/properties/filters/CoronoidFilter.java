package properties.filters;

import constraints.CoronoidConstraint;

public class CoronoidFilter extends DefaultFilter {

	public CoronoidFilter() {
		super(new CoronoidConstraint());
	}

}
