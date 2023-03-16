package modules;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;

public class RhombusModule extends RectangleModule2 {

	@Override
	public void postConstraints() {

		super.postConstraints();

<<<<<<< HEAD
		for (GeneratorCriterion criterion : criterions) {

			if (criterion.getSubject() == Subject.RHOMBUS_DIMENSION) {
				String operatorStr = criterion.getOperatorString();
				generalModel.getProblem().arithm(width, operatorStr, Integer.parseInt(criterion.getValue())).post();
			}
		}

		// ~ generalModel.getProblem().arithm(rotation, "=", 1).post();
		generalModel.getProblem().arithm(height, "=", width).post();
=======
		for (PropertyExpression binaryNumericalExpression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int value = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();
			getGeneralModel().getProblem().arithm(getXW(), operator, value).post();
		}
		getGeneralModel().getProblem().arithm(getXH(), "=", getXW()).post();
>>>>>>> refactoringGenerator
	}
}
