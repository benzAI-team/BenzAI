package constraints;


import generator.GeneralModel;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.ParameterizedExpression;
import generator.properties.model.expression.PropertyExpression;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import utils.HexNeighborhood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CoronoidConstraint extends BenzAIConstraint {

	private UndirectedGraphVar holesGraph;
	private BoolVar[] holesVertices;
	private BoolVar[][] holesEdges;
	private IntVar nbConnectedComponents;

	private ArrayList<Integer> coronenoidBorder;
	private int[] hexagonSparseIndices;
	private int [] hexagonCompactIndices;



	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();
		hexagonSparseIndices = buildHexagonSparseIndices(generalModel);
		System.out.println(hexagonSparseIndices);
		hexagonCompactIndices = buildHexagonCompactIndices(hexagonSparseIndices, getGeneralModel().getDiameter());
		System.out.println(hexagonCompactIndices);
		coronenoidBorder = buildCoronenoidBorder(generalModel.getDiameter(), generalModel.getHexagonIndices());
		System.out.println(coronenoidBorder);
		holesGraph = buildHolesGraphVar(generalModel);
		System.out.println(holesGraph);
		holesVertices = buildHoleVertices(generalModel);
		Arrays.stream(holesVertices).forEach(System.out::print);
		System.out.println(holesVertices);
		holesEdges = buildHoleEdges(generalModel);
		Arrays.stream(holesEdges).forEach(System.out::print);
		System.out.println(holesEdges);
		int nbMaxHoles = computeNbMaxHoles(generalModel.getNbMaxHexagons());
		nbConnectedComponents = generalModel.getProblem().intVar("nb_connected_components", 1, nbMaxHoles + 1);
	}

	private int[] buildHexagonSparseIndices(GeneralModel generalModel) {
		int [] hexagonSparseIndices = new int[generalModel.getNbHexagonsCoronenoid() + 1];
		int index = 0;
		int lastIndex = -1;
		int diameter = generalModel.getDiameter();
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (generalModel.validHexagonIndex(i,j)) {
					int hexagonSparseIndex = generalModel.getHexagonIndex(i, j);
					hexagonSparseIndices[index] = hexagonSparseIndex;
					lastIndex = hexagonSparseIndex;
					index++;
				}
			}
		}
		hexagonSparseIndices[index] = lastIndex + 1;
		return hexagonSparseIndices;
	}

	private int[] buildHexagonCompactIndices(int[] hexagonSparseIndices, int diameter) {
		int[] hexagonCompactIndices = new int[diameter * diameter + 1];
		Arrays.fill(hexagonCompactIndices, -1);
		for (int i = 0; i < hexagonSparseIndices.length; i++)
			hexagonCompactIndices[hexagonSparseIndices[i]] = i;
		return hexagonCompactIndices;
	}

	private ArrayList<Integer> buildCoronenoidBorder(int diameter, int[][] hexagonIndices) {
		ArrayList<Integer> border = new ArrayList<>();
		for (int i = 0; i < diameter; i++) {
			if (i == 0 || i == diameter - 1) {
				for (int j = 0; j < diameter; j++) {
					if (hexagonIndices[i][j] != -1)
						border.add(hexagonCompactIndices[hexagonIndices[i][j]]);
				}
			}
			else {
				for (int j = 0; j < diameter; j++)
					if (hexagonIndices[i][j] != -1) {
						border.add(hexagonCompactIndices[hexagonIndices[i][j]]);
						break;
					}
				for (int j = diameter - 1; j >= 0; j--)
					if (hexagonIndices[i][j] != -1) {
						border.add(hexagonCompactIndices[hexagonIndices[i][j]]);
						break;
					}
			}
		}
		return border;
	}

	private UndirectedGraphVar buildHolesGraphVar(GeneralModel generalModel) {
		int[][] hexagonIndices = generalModel.getHexagonIndices();
		int diameter = generalModel.getDiameter();

		UndirectedGraph GUB = buildHolesGraphVarNodes(hexagonIndices, generalModel);
		UndirectedGraph GLB = new UndirectedGraph(generalModel.getProblem(), generalModel.getNbHexagonsCoronenoid() + 1,
				SetType.LINKED_LIST, false);
		int externalFaceIndex = generalModel.getNbHexagonsCoronenoid();
		GLB.addNode(externalFaceIndex);
		buildHolesGraphVarEdges(generalModel, hexagonIndices, diameter, GUB, externalFaceIndex);
		return generalModel.getProblem().nodeInducedGraphVar("holes", GLB, GUB);
	}

	private static UndirectedGraph buildHolesGraphVarNodes(int[][] hexagonIndices, GeneralModel generalModel) {
		UndirectedGraph GUB = new UndirectedGraph(generalModel.getProblem(), generalModel.getNbHexagonsCoronenoid() + 1,
				SetType.LINKED_LIST, false);
		int hexagonIndex = 0;
		for (int[] indexLine : hexagonIndices) {
			for (int j = 0; j < hexagonIndices.length; j++) {
				if (indexLine[j] != -1) {
					GUB.addNode(hexagonIndex);
					hexagonIndex++;
				}
			}
		}
		GUB.addNode(hexagonIndex); // the external face is in GUB
		return GUB;
	}

	private void buildHolesGraphVarEdges(GeneralModel generalModel, int[][] hexagonIndices, int diameter, UndirectedGraph GUB, int externalFaceIndex) {
		int[][] edges = new int[generalModel.getNbHexagonsCoronenoid() + 1][generalModel.getNbHexagonsCoronenoid() + 1];

		for (int i = 0; i < hexagonIndices.length; i++) {
			for (int j = 0; j < hexagonIndices.length; j++) {
				if (hexagonIndices[i][j] != -1) {
					int hexagonCompactIndex1 = generalModel.getHexagonCompactIndex(hexagonIndices[i][j]);
					int[] N = new int[6];

					for (int k = 0; k < 6; k++)
						N[k] = -1;
					for(HexNeighborhood neighbor : HexNeighborhood.values()){
						int i2 = i + neighbor.dy();
						int j2 = j + neighbor.dx();
						if(i2 >= 0 && i2 <= diameter - 1 && j2 >= 0 && j2 <= diameter - 1)
							N[neighbor.getIndex()] = hexagonIndices[i2][j2];
					}

					for (int k = 0; k < 6; k++) {
						int hexagonCompactIndex2;
						if (N[k] == -1)
							hexagonCompactIndex2 = -1;
						else
							hexagonCompactIndex2 = generalModel.getHexagonCompactIndex(N[k]);
						if (hexagonCompactIndex1 != -1 && hexagonCompactIndex2 != -1) {
							if (edges[hexagonCompactIndex1][hexagonCompactIndex2] == 0) {
								edges[hexagonCompactIndex1][hexagonCompactIndex2] = 1;
								edges[hexagonCompactIndex2][hexagonCompactIndex1] = 1;
								GUB.addEdge(hexagonCompactIndex1, hexagonCompactIndex2);
							}
						}
					}
				}
			}
		}

		// an edge between any border hex and the external face "index"
		for (Integer i : coronenoidBorder)
			GUB.addEdge(externalFaceIndex, i);
	}


	private BoolVar[] buildHoleVertices(GeneralModel generalModel) {
		BoolVar[] holesVertices = new BoolVar[generalModel.getHexBoolVars().length + 1];
		for (int i = 0; i < holesVertices.length; i++)
			holesVertices[i] = generalModel.getProblem().boolVar("nodes[" + i + "]");
		return holesVertices;
	}

	private BoolVar[][] buildHoleEdges(GeneralModel generalModel) {
		BoolVar[][] holesEdges = new BoolVar[holesVertices.length][holesVertices.length];
		int diameter = generalModel.getDiameter();
		Model problem = generalModel.getProblem();
		for (int i = 0; i < diameter * diameter; i++) {
			for (int j = (i + 1); j < diameter * diameter; j++) {
				if (generalModel.sharesSide(i, j)) {
					int hexIndex1 = hexagonCompactIndices[i];
					int hexIndex2 = hexagonCompactIndices[j];
					BoolVar holeEdgeBoolVar = problem.boolVar("edges[" + hexIndex1 + "][" + hexIndex2 + "]");
					holesEdges[hexIndex1][hexIndex2] = holeEdgeBoolVar;
					holesEdges[hexIndex2][hexIndex1] = holeEdgeBoolVar;

					problem.edgeChanneling(holesGraph, holeEdgeBoolVar, hexIndex1, hexIndex2).post();

					// if two hole vertex neighbors exists then their linking edge exists
					BoolVar holeVertexBoolVar1 = holesVertices[hexIndex1];
					BoolVar holeVertexBoolVar2 = holesVertices[hexIndex2];
					BoolVar[] varClause = new BoolVar[] { holeVertexBoolVar1, holeVertexBoolVar2, holeEdgeBoolVar };
					IntIterableRangeSet[] valClause = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
							new IntIterableRangeSet(0), new IntIterableRangeSet(1) };
					problem.getClauseConstraint().addClause(varClause, valClause);
				}
			}

			/*int hexagonCompactIndex = hexagonCompactIndices[i];
			int externalFaceCompactIndex = hexagonCompactIndices[diameter * diameter];
			int externalFaceIndex = holesEdges.length - 1;
			if (hexagonCompactIndex != -1 && externalFaceCompactIndex != -1) {
				BoolVar edgeToExternalFaceBoolVar = problem
						.boolVar("edges[" + externalFaceIndex + "][" + hexagonCompactIndices[i] + "]");
				holesEdges[externalFaceIndex][hexagonCompactIndex] = edgeToExternalFaceBoolVar;
				holesEdges[hexagonCompactIndex][externalFaceIndex] = edgeToExternalFaceBoolVar;
				problem.edgeChanneling(holesGraph, edgeToExternalFaceBoolVar, hexagonCompactIndex, externalFaceCompactIndex).post();
			}*/
		}

		int externalFaceCompactIndex = hexagonCompactIndices[diameter * diameter];
		int externalFaceIndex = holesEdges.length - 1;
		for(int i : coronenoidBorder) {
			int hexagonCompactIndex = hexagonCompactIndices[i];
			if (hexagonCompactIndex != -1 && externalFaceCompactIndex != -1) {
				BoolVar edgeToExternalFaceBoolVar = problem
						.boolVar("edges[" + externalFaceIndex + "][" + hexagonCompactIndices[i] + "]");
				holesEdges[externalFaceIndex][hexagonCompactIndex] = edgeToExternalFaceBoolVar;
				holesEdges[hexagonCompactIndex][externalFaceIndex] = edgeToExternalFaceBoolVar;
				problem.edgeChanneling(holesGraph, edgeToExternalFaceBoolVar, hexagonCompactIndex, externalFaceCompactIndex).post();
			}
		}
		return holesEdges;
	}

	private int computeNbMaxHoles(int nbMaxHexagons) {
		int nbMaxHoles = nbMaxHexagons / 4;
		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((ParameterizedExpression)expression).getOperator();
			if (Objects.equals(operator, "=") || Objects.equals(operator, "<=") || Objects.equals(operator, "<")) {
				int nbHoles = ((BinaryNumericalExpression)expression).getValue();
				if(Objects.equals(operator, "<"))
					nbHoles --;
				if (nbHoles < nbMaxHoles)
					nbMaxHoles = nbHoles;
			}
		}
		System.out.println("NBMAXHOLES " + nbMaxHoles);
		return nbMaxHoles;
	}


	@Override
	public void postConstraints() {
		Model problem = getGeneralModel().getProblem();
		problem.nodesChanneling(holesGraph, holesVertices).post();
		problem.minDegree(holesGraph, 1).post();
		postBenzenoidXORHoles();
		problem.nbConnectedComponents(holesGraph, nbConnectedComponents).post();
        /*if (this.getExpressionList().size() == 1)
			problem.arithm(nbConnectedComponents, ">", 1).post();
		else*/
		//problem.arithm(nbConnectedComponents, ">=", 1).post();
		problem.sum(holesVertices, "<=", getGeneralModel().getNbHexagonsCoronenoid()).post();
		// The external face exists (index = holesVertices - 1)
		//problem.arithm(holesVertices[holesVertices.length - 1], "=", 1).post();

		for (PropertyExpression expression : this.getExpressionList()) {
			String operator = ((BinaryNumericalExpression)expression).getOperator();
			int value = ((BinaryNumericalExpression)expression).getValue();
			problem.arithm(nbConnectedComponents, operator, value + 1).post();
		}
		postHolesExternalFaceConnection();
	}

	@Override
	public void addVariables() {}

	@Override
	public void changeSolvingStrategy() {}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub
	}

	private void postBenzenoidXORHoles() {
		GeneralModel generalModel = getGeneralModel();
		for (int i = 0; i < generalModel.getNbHexagonsCoronenoid(); i++) {
			// At least one
			addBinaryClause(generalModel, generalModel.getHexBoolVar(i), 1, holesVertices[i], 1);
			// At most one
			addBinaryClause(generalModel, generalModel.getHexBoolVar(i), 0, holesVertices[i], 0);
		}
	}

	private void postHolesExternalFaceConnection() {
		int faceExterne = hexagonSparseIndices.length - 1;
		for (Integer i : coronenoidBorder) {
			// If a hex of the border exists then it is linked to the external face
			addBinaryClause(getGeneralModel(), holesVertices[i], 0, holesEdges[i][faceExterne], 1 );
		}
	}

	private void addBinaryClause(GeneralModel generalModel, IntVar var1, int val1, IntVar var2, int val2){
		IntVar[] varClause = new IntVar[2];
		IntIterableRangeSet[] valClause = new IntIterableRangeSet[2];
		varClause[0] = var1;
		valClause[0] = new IntIterableRangeSet(val1);
		varClause[1] = var2;
		valClause[1] = new IntIterableRangeSet(val2);
		generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
	}
}
