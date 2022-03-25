package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;

public class NbCarbonsModule extends Module {

	private int[][] dualGraph;
	ArrayList<GeneratorCriterion> criterions;

	/*
	 * Constraints programming variables
	 */

	private IntVar nbCarbonsVar;

	private IntVar[] benzenoidCarbons;

	private BoolVar[][] xN;
	private BoolVar zero;

	/*
	 * Debug
	 */

	public NbCarbonsModule(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {
		super(generalModel);
		this.criterions = criterions;
	}

	@Override
	public void setPriority() {
		priority = 1;
	}

	@Override
	public void buildVariables() {

		benzenoidCarbons = new IntVar[generalModel.getChanneling().length];
		for (int i = 0; i < generalModel.getChanneling().length; i++)
			benzenoidCarbons[i] = generalModel.getProblem().intVar("nb_carbons_" + i, 0, 6);

		zero = generalModel.getProblem().boolVar(false);

		buildDualGraph();
		buildXN();

		int nbCarbonsMin = 6 * generalModel.getDiameter() * generalModel.getDiameter();
		int nbHydrogensMin = 6 * generalModel.getDiameter() * generalModel.getDiameter();

		int nbCarbonsMax = 0;
		int nbHydrogensMax = 0;

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();
			Operator operator = criterion.getOperator();

			if (operator != Operator.EVEN && operator != Operator.ODD) {

				int value = Integer.parseInt(criterion.getValue());

				if (operator == Operator.EQ) {

					if (subject == Subject.NB_CARBONS) {
						nbCarbonsMin = value;
						nbCarbonsMax = value;
					}

					else if (subject == Subject.NB_HYDROGENS) {
						nbHydrogensMin = value;
						nbHydrogensMax = value;
					}

				}

				else if (operator == Operator.LT || operator == Operator.LEQ) {

					if (subject == Subject.NB_CARBONS && value > nbCarbonsMax)
						nbCarbonsMax = value;

					if (subject == Subject.NB_HYDROGENS && value > nbHydrogensMax)
						nbHydrogensMax = value;
				}

				else if (operator == Operator.GT || operator == Operator.GEQ) {

					if (subject == Subject.NB_CARBONS && value < nbCarbonsMin)
						nbCarbonsMin = value;

					if (subject == Subject.NB_HYDROGENS && value < nbHydrogensMin)
						nbHydrogensMin = value;
				}
			}
		}

		if (nbCarbonsMin == 6 * generalModel.getDiameter() * generalModel.getDiameter())
			nbCarbonsMin = 0;

		if (nbHydrogensMin == 6 * generalModel.getDiameter() * generalModel.getDiameter())
			nbHydrogensMin = 0;

		if (nbCarbonsMax == 0)
			nbCarbonsMax = 6 * generalModel.getDiameter() * generalModel.getDiameter();

		if (nbHydrogensMax == 0)
			nbHydrogensMax = 6 * generalModel.getDiameter() * generalModel.getDiameter();

		nbCarbonsVar = generalModel.getProblem().intVar("nb_carbons", nbCarbonsMin, nbCarbonsMax);
	}

	@Override
	public void postConstraints() {

		/*
		 * Table constraints for carbons
		 */

		Tuples tableCarbons = buildTableCarbons();
		for (int line = 0; line < generalModel.getCoordsMatrix().length; line++) {
			for (int column = 0; column < generalModel.getCoordsMatrix()[line].length; column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					int index = generalModel.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line][column]];
					IntVar[] nH = xN[index];

					IntVar[] tuple2 = new IntVar[] { nH[0], nH[4], nH[5], benzenoidCarbons[index],
							generalModel.getChanneling()[index] };

					generalModel.getProblem().table(tuple2, tableCarbons, "CT+").post();

					for (Variable x : tuple2) {
						System.out.print(x.getName() + "\t");
					}
					System.out.println("");
				}
			}
		}

		Constraint c = generalModel.getProblem().sum(benzenoidCarbons, "=", nbCarbonsVar);
		c.post();

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();
			Operator operator = criterion.getOperator();

			int value = -1;
			if (!criterion.getValue().equals(""))
				value = Integer.parseInt(criterion.getValue());

			if (subject == Subject.NB_CARBONS) {
				if (operator != Operator.EVEN && operator != Operator.ODD) {

					generalModel.getProblem().arithm(nbCarbonsVar, criterion.getOperatorString(), value).post();

				} else {
					if (operator == Operator.EVEN)
						generalModel.getProblem().mod(nbCarbonsVar, 2, 0).post();
					else
						generalModel.getProblem().mod(nbCarbonsVar, 2, 1).post();
				}
			}
		}

		System.out.println(generalModel.getProblem().toString());
	}

	@Override
	public void addWatchedVariables() {
		// DO_NOTHING
	}

	@Override
	public void changeSolvingStrategy() {
		// DO_NOTHING
	}

	@Override
	public void changeWatchedGraphVertices() {
		// DO_NOTHING
	}

	private void buildXN() {

		xN = new BoolVar[generalModel.getChanneling().length][6];

		for (int line = 0; line < dualGraph.length; line++) {
			for (int column = 0; column < dualGraph[line].length; column++) {
				if (dualGraph[line][column] != -1) {
					xN[line][column] = generalModel.getChanneling()[dualGraph[line][column]];
				} else {
					xN[line][column] = zero;
				}
			}
		}

		System.out.print("");
	}

	private void buildDualGraph() {

		dualGraph = new int[generalModel.getChanneling().length][6];

		for (int i = 0; i < dualGraph.length; i++) {
			for (int j = 0; j < dualGraph[i].length; j++) {
				dualGraph[i][j] = -1;
			}
		}

		for (int line = 0; line < generalModel.getCoordsMatrix().length; line++) {
			for (int column = 0; column < generalModel.getCoordsMatrix()[line].length; column++) {

				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					int index = generalModel.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line][column]];

					// High-Right
					if (line > 0 && generalModel.getCoordsMatrix()[line - 1][column] != -1)
						dualGraph[index][0] = generalModel
								.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line - 1][column]];

					// Right
					if (column < generalModel.getCoordsMatrix()[line].length - 1
							&& generalModel.getCoordsMatrix()[line][column + 1] != -1)
						dualGraph[index][1] = generalModel
								.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line][column + 1]];

					// Down-Right
					if (line < generalModel.getCoordsMatrix()[line].length - 1
							&& column < generalModel.getCoordsMatrix()[line].length - 1
							&& generalModel.getCoordsMatrix()[line + 1][column + 1] != -1)
						dualGraph[index][2] = generalModel
								.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line + 1][column + 1]];

					// Down-Left
					if (line < generalModel.getCoordsMatrix()[line].length - 1
							&& generalModel.getCoordsMatrix()[line + 1][column] != -1)
						dualGraph[index][3] = generalModel
								.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line + 1][column]];

					// Left
					if (column > 0 && generalModel.getCoordsMatrix()[line][column - 1] != -1)
						dualGraph[index][4] = generalModel
								.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line][column - 1]];

					// High-Left
					if (line > 0 && column > 0 && generalModel.getCoordsMatrix()[line - 1][column - 1] != -1)
						dualGraph[index][5] = generalModel
								.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line - 1][column - 1]];
				}
			}
		}

		System.out.print("");
	}

	private static Tuples buildTableCarbons() {

		Tuples table = new Tuples(true);

		table.add(0, 0, 0, 6, 1);
		table.add(1, 0, 0, 4, 1);
		table.add(0, 1, 0, 4, 1);
		table.add(0, 0, 1, 4, 1);
		table.add(1, 1, 0, 2, 1);
		table.add(1, 0, 1, 3, 1);
		table.add(0, 1, 1, 3, 1);
		table.add(1, 1, 1, 2, 1);

		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
				for (int k = 0; k < 2; k++)
					table.add(i, j, k, 0, 0);

		return table;
	}

}
