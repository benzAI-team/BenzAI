package properties.filters;

import java.util.ArrayList;

import properties.ModelPropertySet;
import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class HydrogenNumberFilter extends Filter {
	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbHydrogens = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbHydrogens(), ((BinaryNumericalExpression)expression).getOperator(), nbHydrogens))
				return false;
		}
		return true;
	}

}
