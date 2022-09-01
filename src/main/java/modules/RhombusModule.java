package modules;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;

public class RhombusModule extends RectangleModule {

	@Override
	public void postConstraints() {

		super.postConstraints();

		for (PropertyExpression binaryNumericalExpression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int value = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();
			getGeneralModel().getProblem().arithm(getXW(), operator, value).post();
		}
		getGeneralModel().getProblem().arithm(getXH(), "=", getXW()).post();
	}
}
