package constraints;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;


public class CoronenoidConstraint extends BenzAIConstraint {

	private IntVar nbCrowns;
	private IntVar diameter;

	IntVar sub;


	@Override
	public void buildVariables() {
		nbCrowns = getGeneralModel().getProblem().intVar("nb_crowns", 1, getGeneralModel().getNbCrowns());
		diameter = getGeneralModel().getProblem().intVar("diameter", 0, getGeneralModel().getDiameter());
	}

	@Override
	public void postConstraints() {

		int[] nbHexagons = getNbHexagons();
		int[] diameters = getDiameterDomain();

		GeneralModel generalModel = getGeneralModel();
		Constraint[] constraints = new Constraint[nbHexagons.length];
		for (int i = 0; i < constraints.length; i++) {
			Constraint hexagonsCstr = generalModel.getProblem().arithm(generalModel.getNbVerticesVar(), "=",
					nbHexagons[i]);
			Constraint diameterCstr = generalModel.getProblem().arithm(diameter, "=", diameters[i]);

			constraints[i] = generalModel.getProblem().and(hexagonsCstr, diameterCstr);
		}

		generalModel.getProblem().or(constraints).post();

		sub = generalModel.getProblem().intVar("sub", 0, 2 * generalModel.getNbCrowns());
		// sub = nbCrowns - 1
		generalModel.getProblem().sum(new IntVar[] { nbCrowns, generalModel.getProblem().intVar(-1) }, "=", sub).post();
		// diameter = 2 * sub = 2 * (nbCrowns - 1)
		generalModel.getProblem().times(sub, generalModel.getProblem().intVar(2), diameter).post();

		generalModel.getProblem().diameter(generalModel.getGraphVar(), diameter).post();

		for (PropertyExpression binaryNumericalExpression : this.getExpressionList()) {
			int value = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();
			if (value != -1) {
				String operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
				generalModel.getProblem().arithm(nbCrowns, operator, value).post();
			}
		}
	}

	@Override
	public void addVariables() {
		GeneralModel generalModel = getGeneralModel();
		generalModel.addVariable(nbCrowns);
		// generalModel.addWatchedVariable(sub);
		generalModel.addVariable(diameter);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		IntVar[] variables = new IntVar[generalModel.getHexBoolVars().length + 1];
		variables[0] = nbCrowns;
		for (int i = 0; i < generalModel.getHexBoolVars().length; i++) {
			variables[i + 1] = generalModel.getHexBoolVars()[i];
		}

		generalModel.getProblem().getSolver()
				.setSearch(new IntStrategy(variables, new FirstFail(generalModel.getProblem()), new IntDomainMin()));
	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub

	}

	private int[] getNbHexagons() {
		int[] nbHexagons = new int[getGeneralModel().getNbCrowns()];

		for (int nbCrowns = 1; nbCrowns <= getGeneralModel().getNbCrowns(); nbCrowns++) {

			int n = (int) (6.0 * (((nbCrowns * (nbCrowns - 1)) / 2.0)) + 1.0);
			nbHexagons[nbCrowns - 1] = n;
		}

		return nbHexagons;
	}

	private int[] getDiameterDomain() {
		int[] diameters = new int[getGeneralModel().getNbCrowns()];

		for (int nbCrowns = 1; nbCrowns <= getGeneralModel().getNbCrowns(); nbCrowns++) {

			int n = 2 * (nbCrowns - 1);
			diameters[nbCrowns - 1] = n;
		}

		return diameters;
	}
}
