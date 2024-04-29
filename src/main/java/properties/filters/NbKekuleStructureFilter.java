package properties.filters;

import java.util.ArrayList;

import properties.ModelPropertySet;
import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class NbKekuleStructureFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbKekuleStructures = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbKekuleStructures(), ((BinaryNumericalExpression)expression).getOperator(), nbKekuleStructures))
				return false;
		}
		return true;
	}

}
