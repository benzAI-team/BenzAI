package constraints;

import java.util.ArrayList;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.ParameterizedExpression;

public class CoronoidConstraint extends BenzAIConstraint {

	private ArrayList<Integer> border;

	private int[] hexagonsCorrespondances, correspondancesHexagons;

	private UndirectedGraphVar holes;
	private BoolVar[] holesVertices;
	private BoolVar[][] holesEdges;

	private IntVar nbConnectedComponents;

	private int nbMaxHoles;


	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		buildCorrespondancesHexagons();
		buildBorder();
		holes = holesGraphVar("holes");
		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((ParameterizedExpression)expression).getOperator();
			if (operator == "=" || operator == "<=" || operator == "<") {
				int nbHoles = ((BinaryNumericalExpression)expression).getValue();
				if (nbHoles > nbMaxHoles)
					nbMaxHoles = nbHoles;
			}
		}

		if (nbMaxHoles == 0)
			nbMaxHoles = generalModel.getNbMaxHexagons() / 2;

		holesVertices = new BoolVar[generalModel.getChanneling().length + 1];
		for (int i = 0; i < holesVertices.length; i++) {
			BoolVar x = generalModel.getProblem().boolVar("nodes[" + i + "]");
			holesVertices[i] = x;
		}

		holesEdges = new BoolVar[holesVertices.length][holesVertices.length];

		for (int i = 0; i < generalModel.getDiameter() * generalModel.getDiameter(); i++) {
			for (int j = (i + 1); j < generalModel.getDiameter() * generalModel.getDiameter(); j++) {

				if (generalModel.getAdjacencyMatrix()[i][j] == 1) {

					BoolVar x = generalModel.getProblem()
							.boolVar("edges[" + correspondancesHexagons[i] + "][" + correspondancesHexagons[j] + "]");

					int u = correspondancesHexagons[i];
					int v = correspondancesHexagons[j];

					holesEdges[u][v] = x;
					holesEdges[v][u] = x;

					generalModel.getProblem().edgeChanneling(holes, x, u, v).post();

					BoolVar xu = holesVertices[u];
					BoolVar xv = holesVertices[v];

					BoolVar[] varClause = new BoolVar[] { xu, xv, x };
					IntIterableRangeSet[] valClause = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
							new IntIterableRangeSet(0), new IntIterableRangeSet(1) };

					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
				}
			}

			int u = correspondancesHexagons[i];
			int v = correspondancesHexagons[generalModel.getDiameter() * generalModel.getDiameter()];

			BoolVar x = generalModel.getProblem()
					.boolVar("edges[" + (holesEdges.length - 1) + "][" + correspondancesHexagons[i] + "]");

			if (u != -1 && v != -1) {

				holesEdges[(holesEdges.length - 1)][u] = x;
				holesEdges[u][(holesEdges.length - 1)] = x;

				generalModel.getProblem().edgeChanneling(holes, x, u, v).post();
			}
		}

		nbConnectedComponents = generalModel.getProblem().intVar("nb_connected_components", 1, nbMaxHoles + 1);
	}

	private void buildCorrespondancesHexagons() {
		GeneralModel generalModel = getGeneralModel();

		hexagonsCorrespondances = new int[generalModel.getNbHexagonsCoronenoid() + 1];
		int index = 0;
		int lastIndex = -1;

		for (int i = 0; i < generalModel.getDiameter(); i++) {
			for (int j = 0; j < generalModel.getDiameter(); j++) {
				if (generalModel.getHexagonIndices()[i][j] != -1) {
					hexagonsCorrespondances[index] = generalModel.getHexagonIndices()[i][j];
					lastIndex = generalModel.getHexagonIndices()[i][j];
					index++;
				}
			}
		}

		hexagonsCorrespondances[index] = lastIndex + 1;

		correspondancesHexagons = new int[generalModel.getDiameter() * generalModel.getDiameter() + 1];

		for (int i = 0; i < correspondancesHexagons.length; i++)
			correspondancesHexagons[i] = -1;

		for (int i = 0; i < hexagonsCorrespondances.length; i++)
			correspondancesHexagons[hexagonsCorrespondances[i]] = i;

	}

	private void buildBorder() {
		GeneralModel generalModel = getGeneralModel();

		border = new ArrayList<Integer>();
		int diameter = generalModel.getDiameter();
		int[][] coordsMatrix = generalModel.getHexagonIndices();

		for (int i = 0; i < diameter; i++) {

			if (i == 0 || i == diameter - 1) {
				for (int j = 0; j < diameter; j++) {
					if (coordsMatrix[i][j] != -1)
						border.add(correspondancesHexagons[coordsMatrix[i][j]]);
				}
			}

			else {

				for (int j = 0; j < diameter; j++)
					if (coordsMatrix[i][j] != -1) {
						border.add(correspondancesHexagons[coordsMatrix[i][j]]);
						break;
					}

				for (int j = diameter - 1; j >= 0; j--)
					if (coordsMatrix[i][j] != -1) {
						border.add(correspondancesHexagons[coordsMatrix[i][j]]);
						break;
					}
			}
		}
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		generalModel.getProblem().nodesChanneling(holes, holesVertices).post();

		generalModel.getProblem().minDegree(holes, 1).post();
		postBenzenoidXORHoles();

		generalModel.getProblem().nbConnectedComponents(holes, nbConnectedComponents).post();

		if (this.getExpressionList().size() == 1)
			generalModel.getProblem().arithm(nbConnectedComponents, ">", 1).post();
		else
			generalModel.getProblem().arithm(nbConnectedComponents, ">=", 1).post();

		generalModel.getProblem().sum(holesVertices, "<", generalModel.getNbHexagonsCoronenoid()).post();

		generalModel.getProblem().arithm(holesVertices[holesVertices.length - 1], "=", 1).post();

		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)expression).getOperator();
			int value = ((BinaryNumericalExpression)expression).getValue();
			generalModel.getProblem().arithm(nbConnectedComponents, operator, value + 1).post();
		}

		postHolesExternalFaceConnection();

	}

	@Override
	public void addVariables() {
		
	}

	@Override
	public void changeSolvingStrategy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub

	}

	private UndirectedGraphVar holesGraphVar(String name) {
		GeneralModel generalModel = getGeneralModel();

		int[][] matrix = generalModel.getHexagonIndices();
		int diameter = generalModel.getDiameter();

		UndirectedGraph GUB = new UndirectedGraph(generalModel.getProblem(), generalModel.getNbHexagonsCoronenoid() + 1,
				SetType.LINKED_LIST, false);
		UndirectedGraph GLB = new UndirectedGraph(generalModel.getProblem(), generalModel.getNbHexagonsCoronenoid() + 1,
				SetType.LINKED_LIST, false);

		/*
		 * Nodes
		 */

		int index = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (matrix[i][j] != -1) {
					GUB.addNode(index);
					index++;
				}
			}
		}

		GUB.addNode(index);

		/*
		 * Edges
		 */

		int[][] edges = new int[generalModel.getNbHexagonsCoronenoid() + 1][generalModel.getNbHexagonsCoronenoid() + 1];

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {

				if (matrix[i][j] != -1) {

					int u = generalModel.getCorrespondancesHexagons()[matrix[i][j]];

					int[] N = new int[6];

					for (int k = 0; k < 6; k++)
						N[k] = -1;

					if (i > 0)
						N[0] = matrix[i - 1][j];

					if (j + 1 < diameter)
						N[1] = matrix[i][j + 1];

					if (i + 1 < diameter && j + 1 < diameter)
						N[2] = matrix[i + 1][j + 1];

					if (i + 1 < diameter)
						N[3] = matrix[i + 1][j];

					if (j > 0)
						N[4] = matrix[i][j - 1];

					if (i > 0 && j > 0)
						N[5] = matrix[i - 1][j - 1];

					for (int k = 0; k < 6; k++) {
						int v;

						if (N[k] == -1)
							v = -1;
						else
							v = generalModel.getCorrespondancesHexagons()[N[k]];

						if (u != -1 && v != -1) {
							if (edges[u][v] == 0) {

								edges[u][v] = 1;
								edges[v][u] = 1;

								GUB.addEdge(u, v);
							}
						}
					}
				}
			}
		}

		for (Integer i : border)
			GUB.addEdge(index, i);

		return generalModel.getProblem().nodeInducedGraphVar(name, GLB, GUB);
	}

	private void postBenzenoidXORHoles() {
		GeneralModel generalModel = getGeneralModel();

		for (int i = 0; i < generalModel.getNbHexagonsCoronenoid(); i++) {

			// A least one

			IntVar[] varClause = new IntVar[2];
			IntIterableRangeSet[] valClause = new IntIterableRangeSet[2];

			varClause[0] = generalModel.getChanneling()[i];
			valClause[0] = new IntIterableRangeSet(1);
			varClause[1] = holesVertices[i];
			valClause[1] = new IntIterableRangeSet(1);

			generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);

			// A most one

			varClause = new IntVar[2];
			valClause = new IntIterableRangeSet[2];

			varClause[0] = generalModel.getChanneling()[i];
			valClause[0] = new IntIterableRangeSet(0);
			varClause[1] = holesVertices[i];
			valClause[1] = new IntIterableRangeSet(0);

			generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
		}
	}

	private void postHolesExternalFaceConnection() {

		int faceExterne = hexagonsCorrespondances.length - 1;

		for (Integer i : border) {

			BoolVar vertexVariable = holesVertices[i];
			BoolVar edgeVariable = holesEdges[i][faceExterne];

			IntVar[] varClause = new IntVar[] { vertexVariable, edgeVariable };
			IntIterableRangeSet[] valClause = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
					new IntIterableRangeSet(1) };

			getGeneralModel().getProblem().getClauseConstraint().addClause(varClause, valClause);

		}
	}
}
