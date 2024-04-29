package properties.filters;

import java.util.ArrayList;

import properties.ModelPropertySet;
import properties.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class ConcealedNonKekuleanFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		return molecule.getNbKekuleStructures() == 0.0 && molecule.colorShift() == 0;
	}

}
