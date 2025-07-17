package generator.properties.model.filters;

import benzenoid.Benzenoid;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;

import java.util.ArrayList;

public class CarbonNumberFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			if (expression.toString().equals("carbons even")) {
				return molecule.getNbCarbons() % 2 == 0;
			} else if  (expression.toString().equals("carbons odd")) {
				return molecule.getNbCarbons() % 2 != 0;
			}
			else {
				int nbCarbons = ((BinaryNumericalExpression) expression).getValue();

				if (!((BinaryNumericalExpression) expression).test(molecule.getNbCarbons(), ((BinaryNumericalExpression) expression).getOperator(), nbCarbons))
					return false;
			}
		}
		return true;
	}
}
