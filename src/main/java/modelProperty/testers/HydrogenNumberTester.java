package modelProperty.testers;

import java.util.ArrayList;

import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public class HydrogenNumberTester extends Tester {
	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbHydrogens = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbHydrogens(), ((BinaryNumericalExpression)expression).getOperator(), nbHydrogens))
				return false;
		}
		return true;
	}

}
