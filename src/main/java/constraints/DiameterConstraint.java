package constraints;

import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;

public class DiameterConstraint extends BenzAIConstraint {

	private IntVar diameter;

	@Override
	public void buildVariables() {
		diameter = getGeneralModel().getProblem().intVar("diameter", 0, getGeneralModel().getNbMaxHexagons());
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		generalModel.getProblem().diameter(generalModel.getGraphVar(), diameter).post();

		for (PropertyExpression binaryNumericalExpression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int value = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();
			generalModel.getProblem().arithm(diameter, operator, value).post();
		}
	}

	@Override
	public void addVariables() {
		getGeneralModel().addVariable(diameter);
	}

	@Override
	public void changeSolvingStrategy() {

	}

	@Override
	public void changeGraphVertices() {

	}

}
