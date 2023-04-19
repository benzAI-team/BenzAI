package generator.properties.model.checkers;

import java.util.ArrayList;

import generator.properties.model.ModelProperty;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import molecules.Molecule;

public class NbKekuleStructureChecker extends Checker {

	/***
	 * Checks if the number of Kekule structures of the benzenoid respects the expressions
	 */
	@Override
	public boolean checks(Molecule molecule, ModelProperty property) {
		ArrayList<PropertyExpression> expressions = property.getExpressions();
		for(PropertyExpression expression : expressions)
			if (expression instanceof BinaryNumericalExpression) {
				String operator = ((BinaryNumericalExpression)expression).getOperator();
				double value = ((BinaryNumericalExpression)expression).getValue();
				double nbKekuleStructures = molecule.getNbKekuleStructures();
				if(!comparison(nbKekuleStructures , operator, value))
					return false;
			}
		return true;
	}
/***
 * 
 * @param value1
 * @param operator
 * @param value2
 * @return the boolean result of the comparison "value1 operator value2"
 */
	private boolean comparison(double value1, String operator, double value2) {
		switch(operator) {
		case "=" : return value1 == value2;
		case "<" : return value1 < value2;
		case ">" : return value1 > value2;
		case "<=" : return value1 < value2;
		case ">=" : return value1 > value2;
		}
		return false;
	}
}
