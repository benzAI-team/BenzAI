package modelProperty.testers;

import java.util.ArrayList;

import modelProperty.ModelProperty;
import modelProperty.checkers.Checker;
import molecules.Molecule;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;

public class CarbonNumberTester extends Tester {

	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbCarbons = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbNodes(), ((BinaryNumericalExpression)expression).getOperator(), nbCarbons))
				return false;
		}
		return true;
	}
}
