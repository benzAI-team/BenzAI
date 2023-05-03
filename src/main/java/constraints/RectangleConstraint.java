package constraints;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.RectangleExpression;
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

		for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
			for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
				int hexagonIndex = generalModel.getHexagonIndex(columnIndex, lineIndex);
				linesBoolVar[columnIndex][lineIndex] = validIndex(hexagonIndex) ?
						generalModel.getBenzenoidVerticesBVArray(hexagonIndex) :
						zero;
			}
		}
	}

	private void buildColumnsBoolVars() {
		GeneralModel generalModel = getGeneralModel();

		int diameter = generalModel.getDiameter();
		int nbCrowns = generalModel.getNbCrowns();

		int[][] hexagonIndices = generalModel.getHexagonIndices();
		ArrayList<ArrayList<Integer>> linesLists = new ArrayList<>();
		for (int columnIndex = nbCrowns - 1; 0 <= columnIndex; columnIndex--) {
			ArrayList<Integer> lineList = new ArrayList<>(Collections.nCopies(columnIndex, -1));
			int columnIndex2 = columnIndex;
			int lineIndex = 0;
			do {
				lineList.add(hexagonIndices[columnIndex2][lineIndex]);
				columnIndex2++;
				lineIndex++;
			} while (columnIndex2 < diameter && lineIndex < diameter);

			linesLists.add(lineList);
		}

		for (int lineIndex = 1; lineIndex < nbCrowns; lineIndex++) {

			ArrayList<Integer> lineList = new ArrayList<>();

			int columnIndex = 0;
			int lineIndex2 = lineIndex;

			do {
				lineList.add(hexagonIndices[columnIndex][lineIndex2]);
				columnIndex++;
				lineIndex2++;
			} while (lineIndex2 < diameter && columnIndex < diameter);

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
			}
			else {
				columnCorrespondances[center - shift] = i;
				shift++;
			}
		}

		lineCorrespondances = new int[diameter];
		lineCorrespondances[center] = 0;
		for (int i = 1; i < diameter; i++) {

			if (1 == i % 2) {
				lineCorrespondances[center - shift] = i;
			}
			else {
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

		widthVar = model.intVar("nbLines", 1, generalModel.getDiameter());
		heightVar = model.intVar("nb_columns", 1, generalModel.getDiameter());

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

		for (int i = 0; i < generalModel.getNbHexagonsCoronenoid(); i++) {

			BoolVar xi = generalModel.getChanneling()[i];
			int lineIndex = lineCorrespondances[find(xi, linesBoolVar)] + 1;
			int columnIndex = columnCorrespondances[find(xi, columnsBoolVar)] + 1;

			BoolVar lineVar = model.arithm(widthVar, ">=", lineIndex).reify();
			BoolVar columnVar = model.arithm(heightVar, ">=", columnIndex).reify();
			System.out.println(xi.getName() + " (" + columnIndex + "," + lineIndex + ")");
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
		model.arithm(widthVar, ">=", heightVar).post();

		System.out.print("");

	}

	@Override
	public void addVariables() {
		GeneralModel generalModel = getGeneralModel();
		generalModel.addVariable(widthVar);
		generalModel.addVariable(heightVar);
	}

	@Override
	public void changeSolvingStrategy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub

	}

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
