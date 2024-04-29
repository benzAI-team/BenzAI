package properties.filters;

import java.util.ArrayList;


import properties.ModelPropertySet;
import benzenoid.Benzenoid;
import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;

public class CarbonNumberFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbCarbons = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbCarbons(), ((BinaryNumericalExpression)expression).getOperator(), nbCarbons))
				return false;
		}
		return true;
	}
}
