package generator.properties.solver;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.limits.SolutionCounter;

import modelProperty.expression.PropertyExpression;
import modelProperty.expression.SubjectExpression;

public class SolutionNumberSpecifier implements SolverSpecifier {

	@Override
	public void apply(Solver solver, PropertyExpression propertyExpression) {
		solver.addStopCriterion(new SolutionCounter(solver.getModel(), Long.parseLong(propertyExpression.getId())));
	}

}
