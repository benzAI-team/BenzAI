package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.nary.automata.FA.FiniteAutomaton;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Subject;

public class RectangleModule extends Module {

	private int[][] lines;

	/*
	 * Constraint programming variables
	 */

	private BoolVar zero = generalModel.getProblem().boolVar(false);

	private BoolVar[][] C1;
	private BoolVar[][] D1;

	private IntVar[] cSum;
	private IntVar[] dSum;

	protected IntVar xH;
	protected IntVar xW;

	protected ArrayList<GeneratorCriterion> criterions;

	public RectangleModule(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {

		super(generalModel);
		this.criterions = criterions;
	}

	@Override
	public void buildVariables() {

		buildD1();
		buildC1();

		cSum = new IntVar[generalModel.getDiameter()];
		dSum = new IntVar[generalModel.getDiameter()];

		for (int i = 0; i < cSum.length; i++)
			cSum[i] = generalModel.getProblem().intVar("sumC" + i, 0, generalModel.getNbCrowns());

		for (int i = 0; i < dSum.length; i++)
			dSum[i] = generalModel.getProblem().intVar("sumD" + i, 0, generalModel.getDiameter());

		/*
		 * taille de la solution
		 */

		xH = generalModel.getProblem().intVar("height", 1, generalModel.getDiameter());
		xW = generalModel.getProblem().intVar("width", 1, generalModel.getNbCrowns());
	}

	private FiniteAutomaton buildAutomaton() {

		FiniteAutomaton automaton = new FiniteAutomaton();

		int q0 = automaton.addState();
		int q1 = automaton.addState();
		int q2 = automaton.addState();

		automaton.setInitialState(q0);

		automaton.setFinal(q0, q1, q2);

		automaton.addTransition(q0, q0, 0); // q0 ->(0) q0
		automaton.addTransition(q0, q1, 1); // q0 ->(1) q1
		automaton.addTransition(q1, q1, 1); // q1 ->(1) q1
		automaton.addTransition(q1, q2, 0); // q1 ->(0) q2
		automaton.addTransition(q2, q2, 0); // q2 ->(0) q2

		return automaton;
	}

	@Override
	public void postConstraints() {

		/*
		 * Connecting L/C to dSum/cSum
		 */

		for (int i = 0; i < cSum.length; i++) {

			generalModel.getProblem().sum(C1[i], "=", cSum[i]).post();
			generalModel.getProblem().sum(D1[i], "=", dSum[i]).post();
		}

		/*
		 * if a line (resp. a column) exists, then its size has to be xH (resp xW).
		 */

		for (int i = 0; i < cSum.length; i++) {

			generalModel.getProblem().or(generalModel.getProblem().arithm(cSum[i], "=", 0),
					generalModel.getProblem().arithm(cSum[i], "=", xW)).post();
			generalModel.getProblem().or(generalModel.getProblem().arithm(dSum[i], "=", 0),
					generalModel.getProblem().arithm(dSum[i], "=", xH)).post();

		}

		/*
		 * Contiguous lines and columns
		 */

		FiniteAutomaton automaton = buildAutomaton();

		for (int i = 0; i < generalModel.getDiameter(); i++) {

			generalModel.getProblem().regular(C1[i], automaton).post();
			generalModel.getProblem().regular(D1[i], automaton).post();

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
					generalModel.getProblem().arithm(xH, operator, value).post();

				else
					generalModel.getProblem().arithm(xW, operator, value).post();
			}
		}

		generalModel.getProblem().times(xH, xW, generalModel.getNbVerticesVar()).post(); // x * a = z
		generalModel.getProblem().arithm(xH, ">=", xW).post(); // xH >= xw
		// generalModel.getProblem().arithm(generalModel.getChanneling()[0], "=",
		// 1).post(); // The top-left hexagon must be present

		System.out.println(generalModel.getProblem());

	}

	@Override
	public void addWatchedVariables() {
//		generalModel.addWatchedVariable(generalModel.getChanneling());
		generalModel.addWatchedVariable(xW);
		generalModel.addWatchedVariable(xH);
	}

	private void buildD1() {

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

		D1 = new BoolVar[diameter][];

		for (int i = 0; i < lines.size(); i++) {

			BoolVar[] line = new BoolVar[diameter];

			for (int j = 0; j < diameter; j++) {

				int index = lines.get(i).get(j);

				if (index != -1)
					line[j] = generalModel.getWatchedGraphVertices()[index];
				else
					line[j] = zero;

			}

			D1[i] = line;

		}
	}

	private void buildC1() {

		int diameter = generalModel.getDiameter();
		int[][] coordsMatrix = generalModel.getCoordsMatrix();

		C1 = new BoolVar[diameter][diameter];

		for (int column = 0; column < diameter; column++) {

			BoolVar[] c = new BoolVar[diameter];

			for (int line = 0; line < diameter; line++) {

				if (coordsMatrix[line][column] != -1)
					c[line] = generalModel.getWatchedGraphVertices()[coordsMatrix[line][column]];
				else
					c[line] = zero;
			}

			C1[column] = c;
		}

	}

	@Override
	public void changeSolvingStrategy() {

		IntVar[] variables = new IntVar[generalModel.getChanneling().length];

		for (int i = 0; i < generalModel.getChanneling().length; i++)
			variables[i] = generalModel.getChanneling()[i];

		generalModel.getProblem().getSolver()
				.setSearch(new IntStrategy(variables, new FirstFail(generalModel.getProblem()), new IntDomainMax()));
	}

	@Override
	public void changeWatchedGraphVertices() {

	}

	@Override
	public void setPriority() {
		priority = 2;
	}

	@Override
	public String toString() {
		return "RectangleModule";
	}
}
