package constraints;

import generator.GeneralModel;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.RectangleExpression;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;
import java.util.Collections;
public class RectangleConstraint extends BenzAIConstraint {

	private int[] columnCorrespondances;
	private int[] lineCorrespondances;

	private BoolVar[][] linesBoolVar;
	private BoolVar[][] columnsBoolVar;

	private IntVar widthVar;
	private IntVar heightVar;

	private BoolVar zero;

	public RectangleConstraint() {
	}

	private void buildLinesBoolVars() {
		GeneralModel generalModel = getGeneralModel();
		int diameter = generalModel.getDiameter();
		linesBoolVar = new BoolVar[diameter][diameter];

		for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
			for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
				int hexagonIndex = generalModel.getHexagonIndex(lineIndex, columnIndex);
				linesBoolVar[lineIndex][columnIndex] = validIndex(hexagonIndex) ?
						generalModel.getBenzenoidVerticesBVArray(hexagonIndex) :
						zero;
			}
		}
	}

	private void buildColumnsBoolVars() {
		GeneralModel generalModel = getGeneralModel();

		int diameter = generalModel.getDiameter();
		int nbCrowns = generalModel.getNbCrowns();

		int[][] hexagonIndices = generalModel.getHexagonIndicesMatrix();
		ArrayList<ArrayList<Integer>> linesLists = new ArrayList<>();
		for (int lineIndex = nbCrowns - 1; 0 <= lineIndex; lineIndex--) {
			ArrayList<Integer> lineList = new ArrayList<>(Collections.nCopies(lineIndex, -1));
			int lineIndex2 = lineIndex;
			int columnIndex = 0;
			do {
				lineList.add(hexagonIndices[lineIndex2][columnIndex]);
				lineIndex2++;
				columnIndex++;
			} while (lineIndex2 < diameter && columnIndex < diameter);

			linesLists.add(lineList);
		}

		for (int columnIndex = 1; columnIndex < nbCrowns; columnIndex++) {

			ArrayList<Integer> lineList = new ArrayList<>();

			int lineIndex = 0;
			int columnIndex2 = columnIndex;

			do {
				lineList.add(hexagonIndices[lineIndex][columnIndex2]);
				lineIndex++;
				columnIndex2++;
			} while (columnIndex2 < diameter && lineIndex < diameter);

			while (lineList.size() < diameter)
				lineList.add(-1);

			linesLists.add(lineList);
		}

		columnsBoolVar = new BoolVar[diameter][];

		for (int columnIndex = 0; columnIndex < linesLists.size(); columnIndex++) {

			BoolVar[] column = new BoolVar[diameter];

			for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
				int index = linesLists.get(columnIndex).get(lineIndex);
				column[lineIndex] = validIndex(index) ?
						generalModel.getBenzenoidVerticesBVArray(index) :
						zero;
			}
			columnsBoolVar[columnIndex] = column;
		}
	}

	private void buildCorrespondances() {
		GeneralModel generalModel = getGeneralModel();
		int diameter = generalModel.getDiameter();

		columnCorrespondances = new int[diameter];

		int center = (diameter - 1) / 2;
		int shift = 1;

		columnCorrespondances[center] = 0;
		for (int i = 1; i < diameter; i++) {

			if (1 == i % 2) {
				columnCorrespondances[center + shift] = i;
			} else {
				columnCorrespondances[center - shift] = i;
				shift++;
			}
		}

		shift = 1;
		lineCorrespondances = new int[diameter];
		lineCorrespondances[center] = 0;
		for (int i = 1; i < diameter; i++) {

			if (1 == i % 2) {
				lineCorrespondances[center - shift] = i;
			} else {
				lineCorrespondances[center + shift] = i;
				shift++;
			}
		}

	}

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();
		Model model = generalModel.getProblem();

		zero = model.boolVar("zero", false);

		buildCorrespondances();
		buildLinesBoolVars();
		buildColumnsBoolVars();

		widthVar = model.intVar("width", 1, generalModel.getDiameter());
		heightVar = model.intVar("height", 1, generalModel.getDiameter());

	}

	private int find(BoolVar x, BoolVar[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j].equals(x))
					return i;
			}
		}
		return -1;
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();
		Model model = generalModel.getProblem();

		for (int hexIndex = 0; hexIndex < generalModel.getNbHexagonsCoronenoid(); hexIndex++) {

			BoolVar xi = generalModel.getHexBoolVars()[hexIndex];
			int lineIndex = lineCorrespondances[find(xi, linesBoolVar)] + 1;
			int columnIndex = columnCorrespondances[find(xi, columnsBoolVar)] + 1;

			BoolVar lineVar = model.arithm(heightVar, ">=", lineIndex).reify();
			BoolVar columnVar = model.arithm(widthVar, ">=", columnIndex).reify();
			//System.out.println(xi.getName() + " (" + columnIndex + "," + lineIndex + ")");
			//System.out.println(heightVar + " >=" + lineIndex + " " + widthVar + ">=" + columnIndex + ")");
			// Clause 1

			BoolVar[] varClause1 = new BoolVar[] { xi, lineVar };
			IntIterableRangeSet[] valClause1 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
					new IntIterableRangeSet(1) };

			model.getClauseConstraint().addClause(varClause1, valClause1);

			// Clause 2

			BoolVar[] varClause2 = new BoolVar[] { xi, columnVar };
			IntIterableRangeSet[] valClause2 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
					new IntIterableRangeSet(1) };

			model.getClauseConstraint().addClause(varClause2, valClause2);

			// Clause 3

			BoolVar[] varClause3 = new BoolVar[] { lineVar, columnVar, xi };
			IntIterableRangeSet[] valClause3 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
					new IntIterableRangeSet(0), new IntIterableRangeSet(1) };

			model.getClauseConstraint().addClause(varClause3, valClause3);
			//System.out.println("CST:" + xi + "<=>" + lineVar + "/"+columnVar);
		}

		/*
		 * Constraints on number of lines and columns
		 */

		for (PropertyExpression expression : this.getExpressionList()) {
			RectangleExpression rectangleExpression = (RectangleExpression)expression;
			if(0 <= rectangleExpression.getHeight()) {
				model.arithm(heightVar, rectangleExpression.getHeightOperator(), rectangleExpression.getHeight()).post();
				System.out.println("height:" + rectangleExpression.getHeightOperator() + rectangleExpression.getHeight());
			}
			if(0 <= rectangleExpression.getWidth()) {
				model.arithm(widthVar, rectangleExpression.getWidthOperator(), rectangleExpression.getWidth()).post();
				System.out.println("width:" + rectangleExpression.getWidthOperator() + rectangleExpression.getWidth());
			}
		}

		model.times(widthVar, heightVar, generalModel.getNbVerticesVar()).post();
		if(widthInfHeight(getExpressionList()))
			model.arithm(widthVar, "<=", heightVar).post();
		else
			model.arithm(widthVar, ">=", heightVar).post();
	}

	private boolean widthInfHeight(ArrayList<PropertyExpression> expressionList) {
		int minUpperHeight = Integer.MAX_VALUE;
		int minUpperWidth = Integer.MAX_VALUE;
		for(PropertyExpression expression : expressionList) {
			RectangleExpression rectangleExpression = (RectangleExpression) expression;
			if (rectangleExpression.hasWidthUpperBound())
				minUpperWidth = Math.min(minUpperWidth, rectangleExpression.getWidth());
			if (rectangleExpression.hasHeightUpperBound())
				minUpperHeight = Math.min(minUpperHeight, rectangleExpression.getHeight());
		}
		return minUpperWidth <= minUpperHeight;
	}

	@Override
	public void addVariables() {
		GeneralModel generalModel = getGeneralModel();
		generalModel.addVariable(widthVar);
		generalModel.addVariable(heightVar);
	}

	@Override
	public void changeSolvingStrategy() {}

	@Override
	public void changeGraphVertices() {}

	IntVar getWidthVar() {
		return widthVar;
	}

	IntVar getHeightVar() {
		return heightVar;
	}

	private boolean validIndex(int index) {
		return index >= 0;
	}
}
