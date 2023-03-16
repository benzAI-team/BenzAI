package modelProperty.testers;

import java.util.ArrayList;

import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public class HexagonNumberTester extends Tester {

	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbHexagons = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbHexagons(), ((BinaryNumericalExpression)expression).getOperator(), nbHexagons))
				return false;
		}
		return true;
	}

}
