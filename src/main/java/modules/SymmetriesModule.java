package modules;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import solving_modes.SymmetryType;
import utils.Utils;
import modelProperty.expression.ParameterizedExpression;

public class SymmetriesModule extends Module {

	//private SymmetryType symmetryType;

	@Override
	public void buildVariables() {

	}

	@Override
	public void postConstraints() {

		switch (((ParameterizedExpression)this.getExpressionList().get(0)).getOperator()) {

		case "MIRROR":
			postHasMirrorSymmetry();
			break;

		case "ROT_60":
			this.postHasRot60Symmetry();
			break;

		case "ROT_120":
			postHasRot120Symmetry();
			break;

		case "ROT_180":
			postHasRot180Symmetry();
			break;

		case "VERTICAL":
			postHasVerticalSymmetry();
			break;

		case "ROT_120_VERTEX":
			postHasRot120VertexSymmetry();
			break;

		case "ROT_180_EDGE":
			postHasRot180EdgeSymmetry();
			break;
		}
	}

	@Override
	public void addVariables() {

	}

	@Override
	public void changeSolvingStrategy() {

	}

	@Override
	public void changeGraphVertices() {

	}

	private boolean inCoronenoid(int x, int y) {
		GeneralModel generalModel = getGeneralModel();

		if (x < 0 || y < 0 || x >= generalModel.getDiameter() || y >= generalModel.getDiameter())
			return false;
		if (y < generalModel.getNbCrowns())
			return x < generalModel.getNbCrowns() + y;
		else
			return x > y - generalModel.getNbCrowns();
	}

	private boolean inCoronenoid(int i) {
		GeneralModel generalModel = getGeneralModel();
		return inCoronenoid(i % generalModel.getDiameter(), i / generalModel.getDiameter());
	}

	private void postHasSymmetry(Permutation p) {
		int i, j;
		GeneralModel generalModel = getGeneralModel();

		for (j = 0; j < generalModel.getDiameter(); j++)
			for (i = 0; i < generalModel.getDiameter(); i++)
				if (inCoronenoid(i, j))
					if (inCoronenoid(p.from(Utils.getHexagonID(i, j, generalModel.getDiameter())))) {

						BoolVar x = generalModel.getGraphVertices()[Utils.getHexagonID(i, j,
								generalModel.getDiameter())];
						BoolVar y = generalModel.getGraphVertices()[p
								.from(Utils.getHexagonID(i, j, generalModel.getDiameter()))];

						BoolVar[] clauseVariables1 = new BoolVar[] { x, y };
						IntIterableRangeSet[] clauseValues1 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
								new IntIterableRangeSet(1) };

						generalModel.getProblem().getClauseConstraint().addClause(clauseVariables1, clauseValues1);

						BoolVar[] clauseVariables2 = new BoolVar[] { y, x };
						IntIterableRangeSet[] clauseValues2 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
								new IntIterableRangeSet(1) };

						generalModel.getProblem().getClauseConstraint().addClause(clauseVariables2, clauseValues2);

					} else {

						BoolVar x = generalModel.getGraphVertices()[Utils.getHexagonID(i, j,
								generalModel.getDiameter())];

						BoolVar[] clauseVariables = new BoolVar[] { x, x };
						IntIterableRangeSet[] clauseValues = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
								new IntIterableRangeSet(0) };

						generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);

					}
	}

	private void postHasMirrorSymmetry() {
		postHasSymmetry(new Permutation(getGeneralModel().getNbCrowns()) {
			public int from(int i) {
				return diag(i);
			}
		});
	}

	private void postHasRot60Symmetry() {
		postHasSymmetry(new Permutation(getGeneralModel().getNbCrowns(), 1) {
			public int from(int i) {
				return rot(i);
			}
		});
	}

	private void postHasRot120Symmetry() {
		postHasSymmetry(new Permutation(getGeneralModel().getNbCrowns(), 2) {
			public int from(int i) {
				return rot(i);
			}
		});
	}

	private void postHasRot180Symmetry() {
		postHasSymmetry(new Permutation(getGeneralModel().getNbCrowns(), 3) {
			public int from(int i) {
				return rot(i);
			}
		});
	}

	private void postHasVerticalSymmetry() {
		postHasSymmetry(new Permutation(getGeneralModel().getNbCrowns()) {
			public int from(int i) {
				return vert(i);
			}
		});
	}

	private void postHasRot120VertexSymmetry() {
		postHasSymmetry(new Permutation(getGeneralModel().getNbCrowns()) {
			public int from(int i) {
				return rot120vertex(i);
			}
		});
	}

	private void postHasRot180EdgeSymmetry() {
		postHasSymmetry(new Permutation(getGeneralModel().getNbCrowns()) {
			public int from(int i) {
				return rot180edge(i);
			}
		});
	}

}
