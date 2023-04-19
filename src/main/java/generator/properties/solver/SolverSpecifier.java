package generator.properties.solver;

import org.chocosolver.solver.Solver;

import generator.properties.model.expression.PropertyExpression;

public interface SolverSpecifier {
	void apply(Solver solver, PropertyExpression propertyExpression);
}
