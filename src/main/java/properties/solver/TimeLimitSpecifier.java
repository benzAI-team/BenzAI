package properties.solver;

import org.chocosolver.solver.Solver;

import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;

public class TimeLimitSpecifier implements SolverSpecifier {
	@Override
	public void apply(Solver solver, PropertyExpression propertyExpression) {
		long limit = ((BinaryNumericalExpression)propertyExpression).getValue();
		solver.limitTime(limit);
	}

}
