package modelProperty.testers;

import java.util.ArrayList;

import modelProperty.ModelPropertySet;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public class ConcealedNonKekuleanTester extends Tester {

	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		return molecule.getNbKekuleStructures() == 0.0 && molecule.colorShift() == 0;
	}

}
