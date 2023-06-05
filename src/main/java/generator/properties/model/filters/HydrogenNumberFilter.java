package generator.properties.model.filters;

import java.util.ArrayList;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import molecules.Benzenoid;

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
