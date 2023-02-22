package modelProperty.testers;

import java.util.ArrayList;

import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public class IrregularityTester extends Tester {

	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList) {
		for(PropertyExpression expression : propertyExpressionList) {
			int irregularity = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getIrregularity().getXI(), ((BinaryNumericalExpression)expression).getOperator(), irregularity))
				return false;
		}
		return true;
	}

}
