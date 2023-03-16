package modelProperty.testers;

import java.util.ArrayList;

import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import molecules.Molecule;

public class NbKekuleStructureTester extends Tester {

	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		for(PropertyExpression expression : propertyExpressionList) {
			int nbKekuleStructures = ((BinaryNumericalExpression)expression).getValue();
			if(!((BinaryNumericalExpression)expression).test(molecule.getNbKekuleStructures(), ((BinaryNumericalExpression)expression).getOperator(), nbKekuleStructures))
				return false;
		}
		return true;
	}

}
