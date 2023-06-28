package generator.properties.model.filters;

import java.util.ArrayList;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class ConcealedNonKekuleanFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		return molecule.getNbKekuleStructures() == 0.0 && molecule.colorShift() == 0;
	}

}
