package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
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

	private BoolVar[][] L1;
	private BoolVar[][] C1;
	private BoolVar[][] D1;

	private IntVar[] lSum;
	private IntVar[] cSum;
	private IntVar[] dSum;

	protected IntVar xH;
	protected IntVar xW;

	protected ArrayList<GeneratorCriterion> criterions;

	protected BoolVar rotation;

	public RectangleModule(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {

		super(generalModel);
		this.criterions = criterions;
	}

	@Override
	public void buildVariables() {

		rotation = generalModel.getProblem().boolVar("rot");

		buildD1();
		buildC1();
		buildL1();

		lSum = new IntVar[generalModel.getDiameter()];
		cSum = new IntVar[generalModel.getDiameter()];
		dSum = new IntVar[generalModel.getDiameter()];

		for (int i = 0; i < lSum.length; i++)
			lSum[i] = generalModel.getProblem().intVar("sumL" + i, 0, generalModel.getDiameter());

		for (int i = 0; i < cSum.length; i++)
			cSum[i] = generalModel.getProblem().intVar("sumC" + i, 0, generalModel.getDiameter());

		for (int i = 0; i < dSum.length; i++)
			dSum[i] = generalModel.getProblem().intVar("sumD" + i, 0, generalModel.getDiameter());

		/*
		 * taille de la solution
		 */

		xH = generalModel.getProblem().intVar("height", 1, generalModel.getDiameter());
		xW = generalModel.getProblem().intVar("width", 1, generalModel.getDiameter());
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
		 * Connecting L/C to LSum/CSum
		 */

//		for (int i = 0; i < lSum.length; i++) {
//
//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 0),
//					generalModel.getProblem().sum(D1[i], "=", lSum[i]));
//
//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 1),
//					generalModel.getProblem().sum(L1[i], "=", lSum[i]));
//
//		}
//
//		for (int i = 0; i < cSum.length; i++) {
//
//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 0),
//					generalModel.getProblem().sum(C1[i], "=", cSum[i]));
//
//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 1),
//					generalModel.getProblem().sum(C2[i], "=", cSum[i]));
//		}

		for (int i = 0; i < lSum.length; i++) {

			generalModel.getProblem().sum(L1[i], "=", lSum[i]).post();
			generalModel.getProblem().sum(C1[i], "=", cSum[i]).post();
			generalModel.getProblem().sum(D1[i], "=", dSum[i]).post();
		}

		/*
		 * if a line (resp. a column) exists, then its size has to be xH (resp xW).
		 */

//		for (int i = 0; i < lSum.length; i++)
//			generalModel.getProblem().or(generalModel.getProblem().arithm(lSum[i], "=", 0),
//					generalModel.getProblem().arithm(lSum[i], "=", xW)).post();
//
//		for (int i = 0; i < cSum.length; i++)
//			generalModel.getProblem().or(generalModel.getProblem().arithm(cSum[i], "=", 0),
//					generalModel.getProblem().arithm(cSum[i], "=", xH)).post();

		for (int i = 0; i < lSum.length; i++) {

			Constraint lxW = generalModel.getProblem().or(generalModel.getProblem().arithm(lSum[i], "=", 0),
					generalModel.getProblem().arithm(lSum[i], "=", xW));
			Constraint cxH = generalModel.getProblem().or(generalModel.getProblem().arithm(cSum[i], "=", 0),
					generalModel.getProblem().arithm(cSum[i], "=", xH));
			Constraint cxW = generalModel.getProblem().or(generalModel.getProblem().arithm(cSum[i], "=", 0),
					generalModel.getProblem().arithm(cSum[i], "=", xW));
			Constraint dxH = generalModel.getProblem().or(generalModel.getProblem().arithm(dSum[i], "=", 0),
					generalModel.getProblem().arithm(dSum[i], "=", xH));

			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 1), lxW);
			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 1), cxH);

			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 0), cxW);
			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 0), dxH);

		}

		/*
		 * Contiguous lines and columns
		 */

		FiniteAutomaton automaton = buildAutomaton();

		for (int i = 0; i < generalModel.getDiameter(); i++) {

//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 0),
//					generalModel.getProblem().regular(D1[i], automaton));
//
//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 1),
//					generalModel.getProblem().regular(L1[i], automaton));
//
//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 0),
//					generalModel.getProblem().regular(C1[i], automaton));
//
//			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(rotation, "=", 1),
//					generalModel.getProblem().regular(C2[i], automaton));

			generalModel.getProblem().regular(L1[i], automaton).post();
			generalModel.getProblem().regular(C1[i], automaton).post();
			generalModel.getProblem().regular(D1[i], automaton).post();

		}

		/*
		 * Constraints on number of lines and columns
		 */

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();

			if (subject == Subject.RECT_NB_COLUMNS || subject == Subject.RECT_NB_LINES) {

				String operator = criterion.getOperatorString();
				int value = Integer.parseInt(criterion.getValue());

				if (subject == Subject.RECT_NB_COLUMNS)
					generalModel.getProblem().arithm(xH, operator, value).post();

				else if (subject == Subject.RECT_NB_LINES)
					generalModel.getProblem().arithm(xW, operator, value).post();
			}
		}

		generalModel.getProblem().times(xH, xW, generalModel.getNbVerticesVar()).post(); // x * a = z

		System.out.println(generalModel.getProblem());

	}

	@Override
	public void addWatchedVariables() {
//		generalModel.addWatchedVariable(generalModel.getChanneling());
//		generalModel.addWatchedVariable(rotation);
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

	private void buildL1() {

		int diameter = generalModel.getDiameter();
		int[][] coordsMatrix = generalModel.getCoordsMatrix();

		lines = new int[diameter][diameter];

		for (int y = 0; y < diameter; y++) {

			int[] line = lines[y];
			int index = 0;

			for (int x = 0; x < diameter; x++) {

				line[index] = coordsMatrix[y][x];
				index++;
			}
		}

		L1 = new BoolVar[lines.length][];

		for (int i = 0; i < lines.length; i++) {

			int[] line = lines[i];
			L1[i] = new BoolVar[line.length];

			for (int j = 0; j < line.length; j++)
				if (line[j] != -1)
					L1[i][j] = generalModel.getWatchedGraphVertices()[line[j]];
				else
					L1[i][j] = zero;

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

		IntVar[] variables = new IntVar[generalModel.getChanneling().length + 1];

		variables[0] = rotation;
		for (int i = 0; i < generalModel.getChanneling().length; i++)
			variables[i + 1] = generalModel.getChanneling()[i];

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
