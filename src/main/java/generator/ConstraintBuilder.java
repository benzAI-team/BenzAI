package generator;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import constraints.Permutation;
import utils.Coords;
import utils.Utils;

public enum ConstraintBuilder {
	;

	public static void postFillNodesConnection(GeneralModel model) {

		int[] correspondancesHexagons = model.getCorrespondancesHexagons();
		int[][] adjacencyMatrix = model.getAdjacencyMatrix();
		BoolVar[] benzenoidVertices = model.getBenzenoidVerticesBVArray();
		BoolVar[][] benzenoidEdges = model.getBenzenoidEdges();

		// No-Good
		BoolVar[] varTernaryClause = new BoolVar[3];
		IntIterableRangeSet[] valTernaryClause = new IntIterableRangeSet[3];

		BoolVar[] varBinaryClause = new BoolVar[2];
		IntIterableRangeSet[] valBinaryClause = new IntIterableRangeSet[2];

		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = i + 1; j < adjacencyMatrix.length; j++) {
				if (adjacencyMatrix[i][j] == 1) {

					varTernaryClause[0] = benzenoidVertices[i];
					varTernaryClause[1] = benzenoidVertices[j];
					varTernaryClause[2] = benzenoidEdges[correspondancesHexagons[i]][correspondancesHexagons[j]];

					valTernaryClause[0] = new IntIterableRangeSet(0);
					valTernaryClause[1] = new IntIterableRangeSet(0);
					valTernaryClause[2] = new IntIterableRangeSet(1);

					model.getProblem().getClauseConstraint().addClause(varTernaryClause, valTernaryClause);

					varBinaryClause[0] = benzenoidEdges[correspondancesHexagons[i]][correspondancesHexagons[j]];
					varBinaryClause[1] = benzenoidVertices[i];

					valBinaryClause[0] = new IntIterableRangeSet(0);
					valBinaryClause[0] = new IntIterableRangeSet(0);
					valBinaryClause[1] = new IntIterableRangeSet(1);

					model.getProblem().getClauseConstraint().addClause(varBinaryClause, valBinaryClause);

					varBinaryClause[1] = benzenoidVertices[j];
					model.getProblem().getClauseConstraint().addClause(varBinaryClause, valBinaryClause);
				}
			}
		}
	}

	/*
	 * Hole of size 1 constraint
	 */

	private static boolean has6Neighbors(BoolVar[] neighbors) {
		for (BoolVar x : neighbors)
			if (x == null)
				return false;
		return true;
	}

	public static void postNoHolesOfSize1Constraint(GeneralModel model) {

		BoolVar[] benzenoidVertices = model.getBenzenoidVerticesBVArray();
		int[][] coordsMatrix = model.getHexagonIndices();

		BoolVar[] varClause = new BoolVar[7];
		IntIterableRangeSet[] valClause = new IntIterableRangeSet[7];

		for (int i = 0; i < 6; i++)
			valClause[i] = new IntIterableRangeSet(0);
		valClause[6] = new IntIterableRangeSet(1);

		for (int i = 1; i < model.getDiameter() - 1; i++) {
			for (int j = 1; j < model.getDiameter() - 1; j++) {
				if (coordsMatrix[i][j] != -1) {

					BoolVar vertex = benzenoidVertices[coordsMatrix[i][j]];
					BoolVar[] neighbors = model.getNeighbors(i, j);

					if (has6Neighbors(neighbors)) {
                        System.arraycopy(neighbors, 0, varClause, 0, 6);
						varClause[6] = vertex;
						model.getProblem().getClauseConstraint().addClause(varClause, valClause);
					}
				}
			}
		}
	}

	/*
	 * Borders constraint
	 */

	public static void postBordersConstraints(GeneralModel model) {
		if (model.getNbMaxHexagons() > 1) {
			postTopBorderConstraint(model);
			postLeftBordersConstraint(model);
		}
	}

	private static void postLeftBordersConstraint(GeneralModel model) {
		IntIterableRangeSet[] valClause = new IntIterableRangeSet[2 * model.getNbCrowns() - 1];
		BoolVar[] border = new BoolVar[2 * model.getNbCrowns() - 1];

		for (int i = 0; i < model.getNbCrowns(); i++) {
			border[i] = model.getBenzenoidVerticesBVArray()[Utils.getHexagonID(0, i, model.getDiameter())];
			valClause[i] = new IntIterableRangeSet(1);
		}

		for (int i = model.getNbCrowns() - 1; i < 2 * model.getNbCrowns() - 1; i++) {
			border[i] = model.getBenzenoidVerticesBVArray()[Utils.getHexagonID(i - model.getNbCrowns() + 1, i, model.getDiameter())];
			valClause[i] = new IntIterableRangeSet(1);
		}
		model.getProblem().getClauseConstraint().addClause(border, valClause);
	}

	private static void postTopBorderConstraint(GeneralModel model) {
		IntIterableRangeSet[] valClause = new IntIterableRangeSet[model.getNbCrowns()];
		for (int i = 0; i < model.getNbCrowns(); i++)
			valClause[i] = new IntIterableRangeSet(1);

		BoolVar[] border = new BoolVar[model.getNbCrowns()];
		for (int i = 0; i < model.getNbCrowns(); i++)
			border[i] = model.getBenzenoidVerticesBVArray()[Utils.getHexagonID(i, 0, model.getDiameter())];
		model.getProblem().getClauseConstraint().addClause(border, valClause);
	}

	public static boolean inCoronenoid(int x, int y, int diameter, int nbCrowns) {
		if (x < 0 || y < 0 || x >= diameter || y >= diameter)
			return false;
		if (y < nbCrowns)
			return x < nbCrowns + y;
		else
			return x > y - nbCrowns;
	}

	public static boolean inCoronenoid(int i, int diameter, int nbCrowns) {
		return inCoronenoid(i % diameter, i / diameter, diameter, nbCrowns);
	}

	/*
	 * Symmetries breaking
	 */

	private static int postSymmetryBreakingConstraints(Model model, int diameter, int nbCrowns,
			BoolVar[] benzenoidVertices, Permutation p, BoolVar y) {

		int nbClauses = 0;

		int i, j;

		BoolVar yp1 = model.boolVar();
		BoolVar[] varClause = new BoolVar[2];
		IntIterableRangeSet[] valClause = new IntIterableRangeSet[2];
		varClause[0] = y;
		valClause[0] = new IntIterableRangeSet(1);
		varClause[1] = y;
		valClause[1] = new IntIterableRangeSet(1);

		model.getClauseConstraint().addClause(varClause, valClause);
		nbClauses++;

		varClause = new BoolVar[3];
		valClause = new IntIterableRangeSet[3];

		for (j = 0; j < diameter; j++)
			for (i = 0; i < diameter; i++)
				if (inCoronenoid(i, j, diameter, nbCrowns)
						&& inCoronenoid(p.from(Utils.getHexagonID(i, j, diameter)), diameter, nbCrowns)) {
					varClause[0] = y;
					varClause[1] = benzenoidVertices[Utils.getHexagonID(i, j, diameter)];
					varClause[2] = benzenoidVertices[p.from(Utils.getHexagonID(i, j, diameter))];
					valClause[0] = new IntIterableRangeSet(0);
					valClause[1] = new IntIterableRangeSet(1);
					valClause[2] = new IntIterableRangeSet(0);

					model.getClauseConstraint().addClause(varClause, valClause);
					nbClauses++;

					if (j != diameter - 1 || i != diameter - 1) {

						varClause[0] = yp1;
						varClause[1] = y;
						varClause[2] = benzenoidVertices[Utils.getHexagonID(i, j, diameter)];
						valClause[0] = new IntIterableRangeSet(1);
						valClause[1] = new IntIterableRangeSet(0);
						valClause[2] = new IntIterableRangeSet(1);

						model.getClauseConstraint().addClause(varClause, valClause);
						nbClauses++;

						varClause[2] = benzenoidVertices[p.from(Utils.getHexagonID(i, j, diameter))];
						valClause[2] = new IntIterableRangeSet(0);

						model.getClauseConstraint().addClause(varClause, valClause);
						nbClauses++;
					}
					y = yp1;
					yp1 = model.boolVar();
				}

		return nbClauses;
	}

	public static int postSymmetryBreakingConstraints(GeneralModel model) {

		int nbClauses = 0;

		BoolVar y = model.getProblem().boolVar();
		nbClauses += postSymmetryBreakingConstraints(model.getProblem(), model.getDiameter(), model.getNbCrowns(),
				model.getBenzenoidVerticesBVArray(), new Permutation(model.getNbCrowns(), 0) {
					@Override
					public Coords from(Coords indice) {
						return hexAxis(indice);
					}
				}, y);

		for (int i = 1; i < 6; i++) {

			nbClauses += postSymmetryBreakingConstraints(model.getProblem(), model.getDiameter(), model.getNbCrowns(),
					model.getBenzenoidVerticesBVArray(), new Permutation(model.getNbCrowns(), i) {
						@Override
						public Coords from(Coords point) {
							return rot(point);
						}
					}, y);

			nbClauses += postSymmetryBreakingConstraints(model.getProblem(), model.getDiameter(), model.getNbCrowns(),
					model.getBenzenoidVerticesBVArray(), new Permutation(model.getNbCrowns(), i) {
						@Override
						public Coords from(Coords point) {
							return hexAxis(rot(point));
						}
					}, y);
		}

		return nbClauses;
	}

}
