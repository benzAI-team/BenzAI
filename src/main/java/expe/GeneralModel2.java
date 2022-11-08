package expe;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class GeneralModel2 {

	private static Model model;

	private static int nbHexagons;
	private static int nbCrowns;
	private static int diameter;
	private static int nbHexagonsCoronenoid;

	private static int[][] coordsMatrix;

	private static final int star = -1;

	private static Tuples buildTable(int nbNeighbors) {

		Tuples table = new Tuples(true);
		table.setUniversalValue(star);

		int[] tuple0 = new int[nbNeighbors + 1];
		int[] tuple1 = new int[nbNeighbors + 1];

		tuple0[0] = 0;
		tuple1[0] = 1;

		for (int i = 1; i <= nbNeighbors; i++) {
			tuple0[i] = star;
			tuple1[i] = star;
		}

		table.add(tuple0);
		table.add(tuple1);

		for (int n = 2; n < nbHexagons; n++) {
			for (int position = 1; position <= nbNeighbors; position++) {
				int[] tuple = new int[nbNeighbors + 1];
				tuple[0] = n;
				for (int i = 1; i <= nbNeighbors; i++) {
					tuple[i] = star;
				}
				tuple[position] = n - 1;
				table.add(tuple);
			}
		}

		return table;
	}

	private static void buildCoordsMatrix() {

		nbHexagonsCoronenoid = 0;

		coordsMatrix = new int[diameter][diameter];
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				coordsMatrix[i][j] = -1;
			}
		}

		int index = 0;
		int m = (diameter - 1) / 2;

		int shift = diameter - nbCrowns;

		for (int i = 0; i < m; i++) {

			for (int j = 0; j < diameter - shift; j++) {
				coordsMatrix[i][j] = index;
				index++;
				nbHexagonsCoronenoid++;
			}

			shift--;
		}

		for (int j = 0; j < diameter; j++) {
			coordsMatrix[m][j] = index;
			index++;
			nbHexagonsCoronenoid++;
		}

		shift = 1;

		for (int i = m + 1; i < diameter; i++) {

			for (int j = shift; j < diameter; j++) {
				coordsMatrix[i][j] = index;
				index++;
				nbHexagonsCoronenoid++;
			}

			shift++;
		}
	}

	private static List<IntVar> getNeighbors(int i, int j, IntVar[] Y) {

		List<IntVar> neighbors = new ArrayList<>();

		if (i > 0)
			if (coordsMatrix[i - 1][j] != -1)
				neighbors.add(Y[coordsMatrix[i - 1][j]]);

		if (j < diameter - 1)
			if (coordsMatrix[i][j + 1] != -1)
				neighbors.add(Y[coordsMatrix[i][j + 1]]);

		if (i < diameter - 1 && j < diameter - 1)
			if (coordsMatrix[i + 1][j + 1] != -1)
				neighbors.add(Y[coordsMatrix[i + 1][j + 1]]);

		if (i < diameter - 1)
			if (coordsMatrix[i + 1][j] != -1)
				neighbors.add(Y[coordsMatrix[i + 1][j]]);

		if (j > 0)
			if (coordsMatrix[i][j - 1] != -1)
				neighbors.add(Y[coordsMatrix[i][j - 1]]);

		if (i > 0 && j > 0)
			if (coordsMatrix[i - 1][j - 1] != -1)
				neighbors.add(Y[coordsMatrix[i - 1][j - 1]]);

		return neighbors;
	}

	private static void solve() {
		model = new Model("GeneralModel #2");

		BoolVar[] X = new BoolVar[nbHexagonsCoronenoid];
		for (int i = 0; i < nbHexagonsCoronenoid; i++)
			X[i] = model.boolVar("x_" + i);

		IntVar[] Y = new IntVar[nbHexagonsCoronenoid];
		for (int i = 0; i < nbHexagonsCoronenoid; i++)
			Y[i] = model.intVar("y_" + i, 0, nbHexagons);

		model.sum(X, "=", nbHexagons).post();

		model.count(1, Y, model.intVar("limit_count", 1)).post();

		for (int i = 0; i < nbHexagonsCoronenoid; i++)
			model.ifOnlyIf(model.arithm(X[i], "=", 0), model.arithm(Y[i], "=", 0));

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (coordsMatrix[i][j] != -1) {

					List<IntVar> neighbors = getNeighbors(i, j, Y);
					Tuples table = buildTable(neighbors.size());
					IntVar[] scope = new IntVar[1 + neighbors.size()];
					scope[0] = Y[coordsMatrix[i][j]];

					for (int k = 0; k < neighbors.size(); k++)
						scope[k + 1] = neighbors.get(k);

					model.table(scope, table, "CT+").post();
				}
			}
		}

		Solver solver = model.getSolver();
		solver.setSearch(new IntStrategy(X, new FirstFail(model), new IntDomainMax()));

		int nbSolutions = 0;
		while (solver.solve()) {
			for (int i = 0; i < nbHexagonsCoronenoid; i++) {
				if (X[i].getValue() == 1)
					System.out.print(i + " ");
			}
			System.out.println("");
			nbSolutions++;
		}
		System.out.println(nbSolutions + " solutions found");
	}

	private static void initializeValues(String[] args) {
		nbHexagons = 3;

		nbCrowns = (int) Math.floor((((double) ((double) nbHexagons + 1)) / 2.0) + 1.0);

		if (nbHexagons % 2 == 1)
			nbCrowns--;

		diameter = (2 * nbCrowns) - 1;

		buildCoordsMatrix();
	}

	private static String displayTable(Tuples table) {
		return table.toString().replace("][", "]\n[").replace("Allowed tuples: {", "").replace("}", "");
	}

	public static void main(String[] args) {

		initializeValues(args);
		solve();

	}
}
