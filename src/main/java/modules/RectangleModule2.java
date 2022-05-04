package modules;

import java.util.ArrayList;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.GeneratorCriterion;

public class RectangleModule2 extends Module {

	private ArrayList<GeneratorCriterion> criterions;

	private int[] correspondances;

	private BoolVar[][] lines;
	private BoolVar[][] columns;

	private IntVar nbLines;
	private IntVar nbColumns;

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
					line[j] = generalModel.getWatchedGraphVertices()[index];
				else
					line[j] = zero;

			}

			columns[i] = line;

		}
	}

	private void buildCorrespondances() {

		correspondances = new int[generalModel.getDiameter()];

		int center = (generalModel.getDiameter() - 1) / 2;
		correspondances[0] = center;
		int shift = 1;

		for (int i = 1; i < generalModel.getDiameter(); i++) {

			if (i % 2 == 1) {
				correspondances[i] = center - shift;
			}

			else {
				correspondances[i] = center + shift;
				shift++;
			}
		}
	}

	@Override
	public void setPriority() {
		priority = 1;
	}

	@Override
	public void buildVariables() {

		zero = generalModel.getProblem().boolVar("zero", false);

		buildCorrespondances();
		buildLines();
		buildColumns();

		nbLines = generalModel.getProblem().intVar("nbLines", 1, generalModel.getDiameter());
		nbColumns = generalModel.getProblem().intVar("nb_columns", 1, generalModel.getDiameter());

	}

	@Override
	public void postConstraints() {

		/*
		 * Presence clause
		 */

		for (int i = 1; i < generalModel.getDiameter(); i++) {

			BoolVar nbLinesReify = generalModel.getProblem().arithm(nbLines, "<=", i).reify();
			BoolVar[] line = lines[correspondances[i]];
			BoolVar sumLineReify = generalModel.getProblem().sum(line, "=", 0).reify();

		}
	}

	@Override
	public void addWatchedVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeSolvingStrategy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeWatchedGraphVertices() {
		// TODO Auto-generated method stub

	}

}
