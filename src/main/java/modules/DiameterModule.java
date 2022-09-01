package modules;

import java.util.ArrayList;

import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;

public class DiameterModule extends Module {

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
