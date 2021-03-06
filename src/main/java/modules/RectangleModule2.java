package modules;

import java.util.ArrayList;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Subject;

public class RectangleModule2 extends Module {

	protected ArrayList<GeneratorCriterion> criterions;

	private int[] correspondances;
	private int[] correspondances2;

	private BoolVar[][] lines;
	private BoolVar[][] columns;

	protected IntVar width;
	protected IntVar height;

	private BoolVar zero;

	public RectangleModule2(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {
		super(generalModel);
		this.criterions = criterions;
	}

	private void buildLines() {
		lines = new BoolVar[generalModel.getDiameter()][generalModel.getDiameter()];

		for (int i = 0; i < generalModel.getDiameter(); i++) {
			for (int j = 0; j < generalModel.getDiameter(); j++) {
				if (generalModel.getCoordsMatrix()[i][j] != -1)
					lines[i][j] = generalModel.getVG()[generalModel.getCoordsMatrix()[i][j]];
				else
					lines[i][j] = zero;
			}
		}

	}

	private void buildColumns() {

		int diameter = generalModel.getDiameter();
		int nbCrowns = generalModel.getNbCrowns();

		int[][] coordsMatrix = generalModel.getCoordsMatrix();

		ArrayList<ArrayList<Integer>> lines = new ArrayList<>();

		for (int i = nbCrowns - 1; i >= 0; i--) {

			ArrayList<Integer> line = new ArrayList<>();
			for (int j = 0; j < i; j++)
				line.add(-1);

			int li = i;
			int j = 0;

			while (true) {

				line.add(coordsMatrix[li][j]);

				li++;
				j++;

				if (li >= diameter || j >= diameter)
					break;

			}

			lines.add(line);
		}

		for (int j = 1; j < nbCrowns; j++) {

			ArrayList<Integer> line = new ArrayList<>();

			int i = 0;
			int lj = j;

			while (true) {

				line.add(coordsMatrix[i][lj]);

				i++;
				lj++;

				if (lj >= diameter || i >= diameter)
					break;
			}

			while (line.size() < diameter)
				line.add(-1);

			lines.add(line);
		}

		columns = new BoolVar[diameter][];

		for (int i = 0; i < lines.size(); i++) {

			BoolVar[] line = new BoolVar[diameter];

			for (int j = 0; j < diameter; j++) {

				int index = lines.get(i).get(j);

				if (index != -1)
					line[j] = generalModel.getGraphVertices()[index];
				else
					line[j] = zero;

			}

			columns[i] = line;

		}
	}

	private void buildCorrespondances() {

		correspondances = new int[generalModel.getDiameter()];
		correspondances2 = new int[generalModel.getDiameter()];

		int center = (generalModel.getDiameter() - 1) / 2;
		correspondances[0] = center;
		int shift = 1;

		for (int i = 1; i < generalModel.getDiameter(); i++) {

			if (i % 2 == 1) {
				correspondances[i] = center - shift;
				correspondances2[center - shift] = i;
			}

			else {
				correspondances[i] = center + shift;
				correspondances2[center + shift] = i;
				shift++;
			}
		}
	}

	@Override
	public void buildVariables() {

		zero = generalModel.getProblem().boolVar("zero", false);

		buildCorrespondances();
		buildLines();
		buildColumns();

		width = generalModel.getProblem().intVar("nbLines", 1, generalModel.getDiameter());
		height = generalModel.getProblem().intVar("nb_columns", 1, generalModel.getDiameter());

	}

	public int find(BoolVar x, BoolVar[][] matrix) {

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

		for (int i = 0; i < generalModel.getNbHexagonsCoronenoid(); i++) {

			BoolVar xi = generalModel.getChanneling()[i];
			int lineIndex = correspondances2[find(xi, lines)] + 1;
			int columnIndex = correspondances2[find(xi, columns)] + 1;

			BoolVar lineVar = generalModel.getProblem().arithm(width, ">=", lineIndex).reify();
			BoolVar columnVar = generalModel.getProblem().arithm(height, ">=", columnIndex).reify();

			// Clause 1

			BoolVar[] varClause1 = new BoolVar[] { xi, lineVar };
			IntIterableRangeSet[] valClause1 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
					new IntIterableRangeSet(1) };

			generalModel.getProblem().getClauseConstraint().addClause(varClause1, valClause1);

			// Clause 2

			BoolVar[] varClause2 = new BoolVar[] { xi, columnVar };
			IntIterableRangeSet[] valClause2 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
					new IntIterableRangeSet(1) };

			generalModel.getProblem().getClauseConstraint().addClause(varClause2, valClause2);

			// Clause 3

			BoolVar[] varClause3 = new BoolVar[] { lineVar, columnVar, xi };
			IntIterableRangeSet[] valClause3 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
					new IntIterableRangeSet(0), new IntIterableRangeSet(1) };

			generalModel.getProblem().getClauseConstraint().addClause(varClause3, valClause3);
		}

		/*
		 * Constraints on number of lines and columns
		 */

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();

			if (subject == Subject.RECT_WIDTH || subject == Subject.RECT_HEIGHT) {

				String operator = criterion.getOperatorString();
				int value = Integer.parseInt(criterion.getValue());

				if (subject == Subject.RECT_HEIGHT)
					generalModel.getProblem().arithm(height, operator, value).post();

				else
					generalModel.getProblem().arithm(width, operator, value).post();
			}
		}

		generalModel.getProblem().times(width, height, generalModel.getNbVerticesVar()).post();
		generalModel.getProblem().arithm(width, ">=", height).post();

		System.out.print("");

	}

	@Override
	public void addVariables() {
		generalModel.addVariable(width);
		generalModel.addVariable(height);
	}

	@Override
	public void changeSolvingStrategy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub

	}

}
