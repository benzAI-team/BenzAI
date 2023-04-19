package generator.properties.solver;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.limits.SolutionCounter;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;

public class SolutionNumberSpecifier implements SolverSpecifier {

	@Override
	public void apply(Solver solver, PropertyExpression propertyExpression) {
		solver.addStopCriterion(new SolutionCounter(solver.getModel(), ((BinaryNumericalExpression)propertyExpression).getValue()));
	}

}
