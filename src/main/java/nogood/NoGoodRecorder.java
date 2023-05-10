package nogood;

import generator.GeneralModel;
import generator.Solution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;

public abstract class NoGoodRecorder {

	private final GeneralModel model;
	private final Solution solution;

	NoGoodRecorder(GeneralModel model, Solution solution) {
		this.model = model;
		this.solution = solution;
	}

	protected abstract ArrayList<ArrayList<Integer>> computeOccurences();

	public void record() {

		ArrayList<ArrayList<Integer>> translations = computeOccurences();

		BoolVar reified = model.getNbHexagonsReified(getSolution().getNbNodes());

		if (reified == null) {
			BoolVar newVariable = model.getProblem().arithm(model.getNbVerticesVar(), "=", getSolution().getNbNodes())
					.reify();
			model.setNbHexagonsReified(getSolution().getNbNodes(), newVariable);
			reified = newVariable;
		}

		for (ArrayList<Integer> translation : translations) {

			ArrayList<Integer> nogood = new ArrayList<>();

			if (translation.size() > 1) {

				BoolVar[] varClause = new BoolVar[translation.size() + 1];
				IntIterableRangeSet[] valClause = new IntIterableRangeSet[translation.size() + 1];

				for (int i = 0; i < translation.size(); i++) {

					varClause[i] = model.getHexBoolVars()[translation.get(i)];
					valClause[i] = new IntIterableRangeSet(0);

					nogood.add(translation.get(i));
				}

				varClause[varClause.length - 1] = reified;
				valClause[valClause.length - 1] = new IntIterableRangeSet(0);

				if (!model.getNoGoods().contains(nogood)) {
					model.getProblem().getClauseConstraint().addClause(varClause, valClause);
					model.getNoGoods().add(nogood);
				}
			}

			else if (translation.size() == 1) {

				nogood.add(translation.get(0));
				nogood.add(translation.get(0));

				BoolVar[] varClause = new BoolVar[] { model.getHexBoolVars()[translation.get(0)], reified };

				IntIterableRangeSet[] valClause = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
						new IntIterableRangeSet(0) };

				if (!model.getNoGoods().contains(nogood)) {
					model.getProblem().getClauseConstraint().addClause(varClause, valClause);
					model.getNoGoods().add(nogood);
				}
			}
		}

	}

	public Solution getSolution() {
		return solution;
	}

}
