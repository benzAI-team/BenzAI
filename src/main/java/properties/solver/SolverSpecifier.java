package properties.solver;

import org.chocosolver.solver.Solver;

import properties.expression.PropertyExpression;

public interface SolverSpecifier {
	void apply(Solver solver, PropertyExpression propertyExpression);
}
