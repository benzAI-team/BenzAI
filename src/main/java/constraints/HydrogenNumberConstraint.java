package constraints;

import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.ParameterizedExpression;

import java.util.Arrays;
import java.util.Objects;

public class HydrogenNumberConstraint extends BenzAIConstraint {

	private int[][] dualGraph;

	/*
	 * Constraints programming variables
	 */

	private IntVar nbHydrogensVar;

	private BoolVar[][] xN;
	private BoolVar zero;

	private IntVar[] NULL, SOLOS, DUOS, TRIOS, QUATUORS;

	private IntVar N0, N1, N2, N3, N4, N6;


	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		zero = generalModel.getProblem().boolVar(false);

		buildDualGraph();
		buildXN();

		NULL = new IntVar[generalModel.getChanneling().length];
		SOLOS = new IntVar[generalModel.getChanneling().length];
		DUOS = new IntVar[generalModel.getChanneling().length];
		TRIOS = new IntVar[generalModel.getChanneling().length];
		QUATUORS = new IntVar[generalModel.getChanneling().length];

		for (int index = 0; index < generalModel.getChanneling().length; index++) {
			NULL[index] = generalModel.getProblem().intVar("NULL_" + index, new int[] { 0, 1 });
			SOLOS[index] = generalModel.getProblem().intVar("SOLOS_" + index, new int[] { 0, 1, 2 });
			DUOS[index] = generalModel.getProblem().intVar("DUOS_" + index, new int[] { 0, 2 });
			TRIOS[index] = generalModel.getProblem().intVar("TRIOS_" + index, new int[] { 0, 3 });
			QUATUORS[index] = generalModel.getProblem().intVar("QUATUORS_" + index, new int[] { 0, 4 });
		}

		N0 = generalModel.getProblem().intVar("N0", 0, generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N1 = generalModel.getProblem().intVar("N1", 0, 2 * generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N2 = generalModel.getProblem().intVar("N2", 0, 2 * generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N3 = generalModel.getProblem().intVar("N3", 0, 3 * generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N4 = generalModel.getProblem().intVar("N4", 0, 4 * generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N6 = generalModel.getProblem().intVar("N6", new int[] { 0, 6 });

		int nbHydrogensMin = 6 * generalModel.getDiameter() * generalModel.getDiameter();
		int nbHydrogensMax = 0;

		for (PropertyExpression expression : this.getExpressionList()) {

			String operator = ((ParameterizedExpression)expression).getOperator();

			if (!Objects.equals(operator, "even") && !Objects.equals(operator, "odd")) {

				int value = ((BinaryNumericalExpression)expression).getValue();

				if (Objects.equals(operator, "=")) {
					nbHydrogensMin = value;
					nbHydrogensMax = value;
				}

				else if (Objects.equals(operator, "<") || Objects.equals(operator, "<=")) {
					nbHydrogensMax = value;
				}

				else if (Objects.equals(operator, ">") || Objects.equals(operator, ">=")) {
					nbHydrogensMin = value;
				}
			}
		}

		if (nbHydrogensMin == 6 * generalModel.getDiameter() * generalModel.getDiameter())
			nbHydrogensMin = 0;

		if (nbHydrogensMax == 0)
			nbHydrogensMax = 6 * generalModel.getDiameter() * generalModel.getDiameter();

		nbHydrogensVar = generalModel.getProblem().intVar("nb_hydrogens", nbHydrogensMin, nbHydrogensMax);
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		/*
		 * Table constraints for hydrogens
		 */

		Tuples tableHydrogens = buildTable();

		for (int line = 0; line < generalModel.getCoordsMatrix().length; line++) {
			for (int column = 0; column < generalModel.getCoordsMatrix()[line].length; column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					int index = generalModel.getCorrespondancesHexagons()[generalModel.getCoordsMatrix()[line][column]];

					IntVar[] nH = xN[index];
					IntVar[] tuple = new IntVar[] { nH[0], nH[1], nH[2], nH[3], nH[4], nH[5], NULL[index], SOLOS[index],
							DUOS[index], TRIOS[index], QUATUORS[index] };

					generalModel.getProblem().ifThenElse(
							generalModel.getProblem().arithm(generalModel.getChanneling()[index], "=", 1),
							generalModel.getProblem().table(tuple, tableHydrogens, "CT+"),
							generalModel.getProblem().sum(new IntVar[] { NULL[index], SOLOS[index], DUOS[index],
									TRIOS[index], QUATUORS[index] }, "=", 0));
				}
			}
		}

		/*
		 * Sum constraints for N0, N1, N2, N3, N4
		 */

		generalModel.getProblem().sum(NULL, "=", N0).post();
		generalModel.getProblem().sum(SOLOS, "=", N1).post();
		generalModel.getProblem().sum(DUOS, "=", N2).post();
		generalModel.getProblem().sum(TRIOS, "=", N3).post();
		generalModel.getProblem().sum(QUATUORS, "=", N4).post();

		generalModel.getProblem().ifThenElse(generalModel.getProblem().arithm(generalModel.getNbVerticesVar(), "=", 1),
				generalModel.getProblem().arithm(N6, "=", 6), generalModel.getProblem().arithm(N6, "=", 0));
		/*
		 * User constraints
		 */

		generalModel.getProblem().sum(new IntVar[] { N1, N2, N3, N4, N6 }, "=", nbHydrogensVar).post();

		for (PropertyExpression expression : this.getExpressionList()) {

			String operator = ((ParameterizedExpression)expression).getOperator();

			if (!Objects.equals(operator, "even") && !Objects.equals(operator, "odd")) {
				int value = ((BinaryNumericalExpression)expression).getValue();
				generalModel.getProblem().arithm(nbHydrogensVar, ((BinaryNumericalExpression)expression).getOperator(), value).post();
			}
			else {
				if (operator.equals("even"))
					generalModel.getProblem().mod(nbHydrogensVar, 2, 0).post();
				else
					generalModel.getProblem().mod(nbHydrogensVar, 2, 1).post();
			}
		}

	}

	@Override
	public void addVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeSolvingStrategy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub

	}

	private void buildXN() {
		GeneralModel generalModel = getGeneralModel();

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
		GeneralModel generalModel = getGeneralModel();

		dualGraph = new int[generalModel.getChanneling().length][6];

		for (int[] ints : dualGraph) {
			Arrays.fill(ints, -1);
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

	private static Tuples buildTable() {
		Tuples table = new Tuples(true);

		table.add(1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 4);

		table.add(1, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0);
		table.add(0, 1, 1, 0, 0, 0, 0, 0, 0, 3, 0);
		table.add(0, 0, 1, 1, 0, 0, 0, 0, 0, 3, 0);
		table.add(0, 0, 0, 1, 1, 0, 0, 0, 0, 3, 0);
		table.add(0, 0, 0, 0, 1, 1, 0, 0, 0, 3, 0);
		table.add(1, 0, 0, 0, 0, 1, 0, 0, 0, 3, 0);

		table.add(1, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0);
		table.add(0, 1, 0, 0, 1, 0, 0, 2, 0, 0, 0);
		table.add(0, 0, 1, 0, 0, 1, 0, 2, 0, 0, 0);

		table.add(1, 0, 1, 0, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 1, 0, 1, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 0);
		table.add(1, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0);
		table.add(0, 1, 0, 0, 0, 1, 0, 0, 2, 0, 0);

		table.add(1, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 1, 1, 1, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 1, 1, 1, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 0, 1, 1, 1, 0, 0, 2, 0, 0);
		table.add(1, 0, 0, 0, 1, 1, 0, 0, 2, 0, 0);
		table.add(1, 1, 0, 0, 0, 1, 0, 0, 2, 0, 0);

		table.add(1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0);

		table.add(1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0);

		table.add(1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0);
		table.add(0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0);
		table.add(1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0);

		table.add(1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

		return table;
	}
}
