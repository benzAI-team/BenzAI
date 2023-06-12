package generator.properties.model.filters;

import java.util.ArrayList;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class IrregularityFilter extends Filter {

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int irregularity = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getIrregularity().get().getXI(), ((BinaryNumericalExpression)expression).getOperator(), irregularity))
				return false;
		}
		return true;
	}

}
