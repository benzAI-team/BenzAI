package constraints;

import generator.GeneralModel;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;

public class HexagonNumberConstraint extends BenzAIConstraint {

	@Override
	public void buildVariables() {
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)expression).getOperator();
			int value = ((BinaryNumericalExpression)expression).getValue();
			System.out.println("hexagons " + getGeneralModel().getNbVerticesVar() + " " + operator + " " + value);
			this.getGeneralModel().getChocoModel().arithm(getGeneralModel().getNbVerticesVar(), operator, value).post();
		}
		System.out.println(generalModel.getProblem().toString());
	}

	@Override
	public void addVariables() {
		// DO_NOTHING
	}

	@Override
	public void changeSolvingStrategy() {
		// DO_NOTHING
	}

	@Override
	public void changeGraphVertices() {
		// DO_NOTHING
	}



}