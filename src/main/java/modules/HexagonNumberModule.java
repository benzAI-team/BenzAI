package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;

public class HexagonNumberModule extends Module {

	@Override
	public void buildVariables() {
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)expression).getOperator();
			int value = ((BinaryNumericalExpression)expression).getValue();
			System.out.println("hexagons " + getGeneralModel().getNbVerticesVar() + " " +operator + " " + value);
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