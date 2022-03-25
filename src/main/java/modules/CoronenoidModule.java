package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;

public class CoronenoidModule extends Module {

	private ArrayList<GeneratorCriterion> criterions;

	private IntVar nbCrowns;
	private IntVar diameter;

	IntVar sub;

	public CoronenoidModule(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {
		super(generalModel);
		this.criterions = criterions;
	}

	@Override
	public void setPriority() {
		// TODO Auto-generated method stub

	}

	@Override
	public void buildVariables() {
		nbCrowns = generalModel.getProblem().intVar("nb_crowns", 1, generalModel.getNbCrowns());
		diameter = generalModel.getProblem().intVar("diameter", 0, generalModel.getDiameter());
	}

	@Override
	public void postConstraints() {

		int[] nbHexagons = getNbHexagons();
		int[] diameters = getDiameterDomain();

		Constraint[] constraints = new Constraint[nbHexagons.length];
		for (int i = 0; i < constraints.length; i++) {
			Constraint hexagonsCstr = generalModel.getProblem().arithm(generalModel.getNbVerticesVar(), "=",
					nbHexagons[i]);
			Constraint diameterCstr = generalModel.getProblem().arithm(diameter, "=", diameters[i]);

			constraints[i] = generalModel.getProblem().and(new Constraint[] { hexagonsCstr, diameterCstr });
		}

		generalModel.getProblem().or(constraints).post();

		sub = generalModel.getProblem().intVar("sub", 0, 2 * generalModel.getNbCrowns());
		// sub = nbCrowns - 1
		generalModel.getProblem().sum(new IntVar[] { nbCrowns, generalModel.getProblem().intVar(-1) }, "=", sub).post();
		// diameter = 2 * sub = 2 * (nbCrowns - 1)
		generalModel.getProblem().times(sub, generalModel.getProblem().intVar(2), diameter).post();

		// diameter =
		// nbCrowns.add(generalModel.getProblem().intVar(-1)).mul(generalModel.getProblem().intVar(2)).intVar();

		generalModel.getProblem().diameter(generalModel.getWatchedGraphVar(), diameter).post();

		for (GeneratorCriterion criterion : criterions) {
			if (criterion.getSubject() == Subject.NB_CROWNS && !criterion.getValue().equals("Unspecified")) {

				Operator operator = criterion.getOperator();
				int value = Integer.parseInt(criterion.getValue());

				generalModel.getProblem().arithm(nbCrowns, criterion.getOperatorString(), value).post();
			}
		}
	}

	@Override
	public void addWatchedVariables() {
		generalModel.addWatchedVariable(nbCrowns);
		// generalModel.addWatchedVariable(sub);
		generalModel.addWatchedVariable(diameter);
	}

	@Override
	public void changeSolvingStrategy() {

		IntVar[] variables = new IntVar[generalModel.getChanneling().length + 1];
		variables[0] = nbCrowns;
		for (int i = 0; i < generalModel.getChanneling().length; i++) {
			variables[i + 1] = generalModel.getChanneling()[i];
		}

		generalModel.getProblem().getSolver()
				.setSearch(new IntStrategy(variables, new FirstFail(generalModel.getProblem()), new IntDomainMin()));
	}

	@Override
	public void changeWatchedGraphVertices() {
		// TODO Auto-generated method stub

	}

	private int[] getNbHexagons() {
		int[] nbHexagons = new int[generalModel.getNbCrowns()];

		for (int nbCrowns = 1; nbCrowns <= generalModel.getNbCrowns(); nbCrowns++) {

			int n = (int) (6.0 * (((nbCrowns * (nbCrowns - 1)) / 2.0)) + 1.0);
			nbHexagons[nbCrowns - 1] = n;
		}

		return nbHexagons;
	}

	private int[] getDiameterDomain() {
		int[] diameters = new int[generalModel.getNbCrowns()];

		for (int nbCrowns = 1; nbCrowns <= generalModel.getNbCrowns(); nbCrowns++) {

			int n = 2 * (nbCrowns - 1);
			diameters[nbCrowns - 1] = n;
		}

		return diameters;
	}
}
