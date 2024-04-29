package properties.filters;

import constraints.CatacondensedConstraint;

public class CatacondensedFilter extends DefaultFilter {
	public CatacondensedFilter(){
		super(new CatacondensedConstraint());
	}
}
