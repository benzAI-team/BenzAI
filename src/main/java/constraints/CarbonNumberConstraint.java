package constraints;


import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import generator.GeneralModel;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.ParameterizedExpression;

import java.util.Arrays;
import java.util.Objects;

public class CarbonNumberConstraint extends BenzAIConstraint {

	private int[][] dualGraph;

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

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		benzenoidCarbons = new IntVar[generalModel.getChanneling().length];
		for (int i = 0; i < generalModel.getChanneling().length; i++)
			benzenoidCarbons[i] = generalModel.getProblem().intVar("nb_carbons_" + i, 0, 6);

		zero = generalModel.getProblem().boolVar(false);

		buildDualGraph();
		buildXN();

		int nbCarbonsMin = 6 * generalModel.getDiameter() * generalModel.getDiameter();

		int nbCarbonsMax = 0;

		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((ParameterizedExpression)expression).getOperator();

			if (!Objects.equals(operator, "even") && !Objects.equals(operator, "odd")) {
				int value = ((BinaryNumericalExpression)expression).getValue();
				if (Objects.equals(operator, "=")) {
						nbCarbonsMin = value;
						nbCarbonsMax = value;
				}
				else if (Objects.equals(operator, "<") || Objects.equals(operator, "<="))
						nbCarbonsMax = value;
				else if (Objects.equals(operator, ">") || Objects.equals(operator, ">="))
						nbCarbonsMin = value;

			}
		}

		if (nbCarbonsMin == 6 * generalModel.getDiameter() * generalModel.getDiameter())
			nbCarbonsMin = 0;

		if (nbCarbonsMax == 0)
			nbCarbonsMax = 6 * generalModel.getDiameter() * generalModel.getDiameter();

		nbCarbonsVar = generalModel.getProblem().intVar("nb_carbons", nbCarbonsMin, nbCarbonsMax);
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		/*
		 * Table constraints for carbons
		 */

		Tuples tableCarbons = buildTableCarbons();
		for (int line = 0; line < generalModel.getHexagonIndices().length; line++) {
			for (int column = 0; column < generalModel.getHexagonIndices()[line].length; column++) {
				if (generalModel.getHexagonIndices()[line][column] != -1) {

					int index = generalModel.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line][column]];
					IntVar[] nH = xN[index];

					IntVar[] tuple2 = new IntVar[] { nH[0], nH[4], nH[5], benzenoidCarbons[index],
							generalModel.getChanneling()[index] };

					generalModel.getProblem().table(tuple2, tableCarbons, "CT+").post();

					for (Variable x : tuple2) {
						System.out.print(x.getName() + "\t");
					}
					System.out.println();
				}
			}
		}

		Constraint c = generalModel.getProblem().sum(benzenoidCarbons, "=", nbCarbonsVar);
		c.post();

		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((ParameterizedExpression)expression).getOperator();

			if (!Objects.equals(operator, "even") && !Objects.equals(operator, "odd")) {
				int value = ((BinaryNumericalExpression)expression).getValue();
				generalModel.getProblem().arithm(nbCarbonsVar, ((ParameterizedExpression)expression).getOperator(), value).post();

			} else {
				if ("even".equals(operator))
					generalModel.getProblem().mod(nbCarbonsVar, 2, 0).post();
				else
					generalModel.getProblem().mod(nbCarbonsVar, 2, 1).post();
			}
		}

		System.out.println(generalModel.getProblem().toString());
	}

	@Override
	public void addVariables() {
		// DO_NOTHING
	}

	@Override
	public void changeSolvingStrategy() {
		// DO_NOTHING
	}

	@Override
	public void changeGraphVertices() {
		// DO_NOTHING
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

		for (int line = 0; line < generalModel.getHexagonIndices().length; line++) {
			for (int column = 0; column < generalModel.getHexagonIndices()[line].length; column++) {

				if (generalModel.getHexagonIndices()[line][column] != -1) {

					int index = generalModel.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line][column]];

					// High-Right
					if (line > 0 && generalModel.getHexagonIndices()[line - 1][column] != -1)
						dualGraph[index][0] = generalModel
								.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line - 1][column]];

					// Right
					if (column < generalModel.getHexagonIndices()[line].length - 1
							&& generalModel.getHexagonIndices()[line][column + 1] != -1)
						dualGraph[index][1] = generalModel
								.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line][column + 1]];

					// Down-Right
					if (line < generalModel.getHexagonIndices()[line].length - 1
							&& column < generalModel.getHexagonIndices()[line].length - 1
							&& generalModel.getHexagonIndices()[line + 1][column + 1] != -1)
						dualGraph[index][2] = generalModel
								.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line + 1][column + 1]];

					// Down-Left
					if (line < generalModel.getHexagonIndices()[line].length - 1
							&& generalModel.getHexagonIndices()[line + 1][column] != -1)
						dualGraph[index][3] = generalModel
								.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line + 1][column]];

					// Left
					if (column > 0 && generalModel.getHexagonIndices()[line][column - 1] != -1)
						dualGraph[index][4] = generalModel
								.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line][column - 1]];

					// High-Left
					if (line > 0 && column > 0 && generalModel.getHexagonIndices()[line - 1][column - 1] != -1)
						dualGraph[index][5] = generalModel
								.getCorrespondancesHexagons()[generalModel.getHexagonIndices()[line - 1][column - 1]];
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
