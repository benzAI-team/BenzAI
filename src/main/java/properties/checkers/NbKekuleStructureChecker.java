package properties.checkers;

import java.util.ArrayList;

import properties.ModelProperty;
import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;
import benzenoid.Benzenoid;

public class NbKekuleStructureChecker extends Checker {

	/***
	 * Checks if the number of Kekule structures of the benzenoid respects the expressions
	 */
	@Override
	public boolean checks(Benzenoid molecule, ModelProperty property) {
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
 * @return the boolean result of the comparison "value1 operator value2"
 */
	private boolean comparison(double value1, String operator, double value2) {
		switch(operator) {
		case "=" : return value1 == value2;
		case "<" : return value1 < value2;
		case ">" : return value1 > value2;
		case "<=" : return value1 <= value2;
		case ">=" : return value1 >= value2;
		}
		return false;
	}
}
