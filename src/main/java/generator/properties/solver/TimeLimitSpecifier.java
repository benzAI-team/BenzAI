package generator.properties.solver;

import org.chocosolver.solver.Solver;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;

public class TimeLimitSpecifier implements SolverSpecifier {
	@Override
	public void apply(Solver solver, PropertyExpression propertyExpression) {
		long limit = ((BinaryNumericalExpression)propertyExpression).getValue();
		solver.limitTime(limit);
	}

}
