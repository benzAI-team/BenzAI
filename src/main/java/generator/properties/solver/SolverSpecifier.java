package generator.properties.solver;

import org.chocosolver.solver.Solver;

import modelProperty.expression.PropertyExpression;

public interface SolverSpecifier {
	void apply(Solver solver, PropertyExpression propertyExpression);
}
