package constraints;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;

public class RhombusConstraint extends RectangleConstraint {

	@Override
	public void postConstraints() {

		super.postConstraints();

		for (PropertyExpression binaryNumericalExpression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int value = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();
			getGeneralModel().getProblem().arithm(getWidth(), operator, value).post();
		}
		getGeneralModel().getProblem().arithm(getHeight(), "=", getWidth()).post();
	}
}
