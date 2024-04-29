package properties.solver;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.limits.SolutionCounter;

import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;

public class SolutionNumberSpecifier implements SolverSpecifier {

	@Override
	public void apply(Solver solver, PropertyExpression propertyExpression) {
		solver.addStopCriterion(new SolutionCounter(solver.getModel(), ((BinaryNumericalExpression)propertyExpression).getValue()));
	}

}
