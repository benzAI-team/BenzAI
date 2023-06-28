package generator.properties.model.filters;

import java.util.ArrayList;


import generator.properties.model.ModelPropertySet;
import benzenoid.Benzenoid;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;

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
