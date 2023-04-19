package generator.properties.model.filters;

import java.util.ArrayList;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import molecules.Molecule;

public class NbKekuleStructureFilter extends Filter {

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
