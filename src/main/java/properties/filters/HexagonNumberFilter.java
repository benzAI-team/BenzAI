package properties.filters;

import java.util.ArrayList;

import properties.ModelPropertySet;
import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class HexagonNumberFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbHexagons = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbHexagons(), ((BinaryNumericalExpression)expression).getOperator(), nbHexagons))
				return false;
		}
		return true;
	}

}
