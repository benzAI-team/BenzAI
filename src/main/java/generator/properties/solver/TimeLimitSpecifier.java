package generator.properties.solver;

import org.chocosolver.solver.Solver;

import modelProperty.expression.PropertyExpression;
import modelProperty.expression.SubjectExpression;

public class TimeLimitSpecifier implements SolverSpecifier {
	@Override
	public void apply(Solver solver, PropertyExpression propertyExpression) {
		solver.limitTime(propertyExpression.getId());
	}

}
