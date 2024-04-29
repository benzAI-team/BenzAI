package properties.filters;

import java.util.ArrayList;

import properties.ModelPropertySet;
import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class IrregularityFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int irregularity = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getIrregularity().getXI(), ((BinaryNumericalExpression)expression).getOperator(), irregularity))
				return false;
		}
		return true;
	}

}
