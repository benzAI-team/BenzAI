package modelProperty.testers;

import java.util.ArrayList;


import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.checkers.Checker;
import molecules.Molecule;
import utils.Utils;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;

public class CarbonNumberTester extends Tester {

	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbCarbons = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbNodes(), ((BinaryNumericalExpression)expression).getOperator(), nbCarbons))
				return false;
		}
		return true;
	}
}
