package constraints;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import utils.Coords;
import utils.Utils;
import generator.properties.model.expression.ParameterizedExpression;

public class SymmetriesConstraint extends BenzAIConstraint {
	@Override
	public void buildVariables() {}

	@Override
	public void postConstraints() {
		switch (((ParameterizedExpression)this.getExpressionList().get(0)).getOperator()) {
		case "C_2v(a) \"face-mirror\"":
			postHasHexagonAxisSymmetry();
			break;
		case "C_6h \"(face)-60-rotation\"" : 
			postHasRot60Symmetry();
			break;
		case "C_3h(i) \"face-120-rotation\"":			
			postHasRot120Symmetry();
			break;
		case "C_2h(i) \"face-180-rotation\"": 
			postHasRot180Symmetry();
			break;
		case "C_2v(b) \"edge-mirror\"" : 
			postHasEdgeAxisSymmetry();
			break;
		case "C_3h(ii) \"vertex-120-rotation\"" : 
			postHasRot120VertexSymmetry();
			break;
		case "C_2h(ii) \"edge-180-rotation\"" : 
			postHasRot180EdgeSymmetry();
			break;
		case "D_6h \"(face)-60-rotation+(edge)-mirror\"" : 
			postHasEdgeAxisSymmetry();
			postHasRot60Symmetry();
			break;
		case "D_3h(ii) \"vertex-120-rotation+(edge)-mirror\"" :
			postHasRot120VertexSymmetry();
			postHasEdgeAxisSymmetry();
			break;
		case "D_3h(ia) \"face-120-rotation+face-mirror\"" :
			postHasRot120Symmetry();
			postHasHexagonAxisSymmetry();
			break;
		case "D_3h(ib) \"face-120-rotation+edge-mirror\"" :
			postHasRot120Symmetry();
			postHasEdgeAxisSymmetry();
			break;
		case "D_2h(ii) \"edge-180-rotation+edge-mirror\"" :
			postHasRot180EdgeSymmetry();
			postHasEdgeAxisSymmetry();
			break;
		case "D_2h(i) \"face-180-rotation+edge-mirror\"" : 
			postHasRot180Symmetry();
			postHasEdgeAxisSymmetry();//??
			break;
		case "C_s \"no-symmetry\"" :
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

						BoolVar x = generalModel.getBenzenoidVerticesBVArray(Utils.getHexagonID(i, j,
								generalModel.getDiameter()));
						BoolVar y = generalModel.getBenzenoidVerticesBVArray(p
								.from(Utils.getHexagonID(i, j, generalModel.getDiameter())));
//System.out.println("S: ("+ i + "," + j + ") :" + Utils.getHexagonID(i, j, generalModel.getDiameter()) + " " + p.from(Utils.getHexagonID(i, j, generalModel.getDiameter())));
						BoolVar[] clauseVariables1 = new BoolVar[] { x, y };
						IntIterableRangeSet[] clauseValues1 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
								new IntIterableRangeSet(1) };

						generalModel.getProblem().getClauseConstraint().addClause(clauseVariables1, clauseValues1);

						BoolVar[] clauseVariables2 = new BoolVar[] { y, x };
						IntIterableRangeSet[] clauseValues2 = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
								new IntIterableRangeSet(1) };

						generalModel.getProblem().getClauseConstraint().addClause(clauseVariables2, clauseValues2);

					} else {

						BoolVar x = generalModel.getBenzenoidVerticesBVArray(Utils.getHexagonID(i, j,
								generalModel.getDiameter()));

						BoolVar[] clauseVariables = new BoolVar[] { x, x };
						IntIterableRangeSet[] clauseValues = new IntIterableRangeSet[] { new IntIterableRangeSet(0),
								new IntIterableRangeSet(0) };

						generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);

					}
	}

	private void postHasHexagonAxisSymmetry() {
		postHasSymmetry(new HexagonAxisSymmetry());
	}

	private void postHasRot60Symmetry() {
		postHasSymmetry(new Rot60Symmetry());
	}

	private void postHasRot120Symmetry() {
		postHasSymmetry(new Rot120Symmetry());
	}

	private void postHasRot180Symmetry() {
		postHasSymmetry(new Rot180Symmetry());
	}

	private void postHasEdgeAxisSymmetry() {
		postHasSymmetry(new EdgeAxisSymmetry());
	}

	private void postHasRot120VertexSymmetry() {
		postHasSymmetry(new Rot120VertexSymmetry());
	}

	private void postHasRot180EdgeSymmetry() {
		postHasSymmetry(new Rot180EdgeSymmetry());
	}

	private class HexagonAxisSymmetry extends Permutation {
		HexagonAxisSymmetry() {
			super(SymmetriesConstraint.this.getGeneralModel().getNbCrowns());
		}
		@Override
		public Coords from(Coords point) {
			return hexAxis(point);
		}
	}

	private class Rot60Symmetry extends Permutation {
		Rot60Symmetry() {
			super(SymmetriesConstraint.this.getGeneralModel().getNbCrowns(), 1);
		}

		public Coords from(Coords point) {
			return rot(point);
		}
	}

	private class Rot120Symmetry extends Permutation {
		Rot120Symmetry() {
			super(SymmetriesConstraint.this.getGeneralModel().getNbCrowns(), 2);
		}

		public Coords from(Coords point) {
			return rot(point);
		}
	}

	private class Rot180Symmetry extends Permutation {
		Rot180Symmetry() {
			super(SymmetriesConstraint.this.getGeneralModel().getNbCrowns(), 3);
		}

		public Coords from(Coords point) {
			return rot(point);
		}
	}

	private class EdgeAxisSymmetry extends Permutation {
		EdgeAxisSymmetry() {
			super(SymmetriesConstraint.this.getGeneralModel().getNbCrowns());
		}

		public Coords from(Coords point) {
			return edgeAxis(point);
		}
	}

	private class Rot120VertexSymmetry extends Permutation {
		Rot120VertexSymmetry() {
			super(SymmetriesConstraint.this.getGeneralModel().getNbCrowns());
		}

		public Coords from(Coords point) {
			return rot120topVertex(point);
		}
	}

	private class Rot180EdgeSymmetry extends Permutation {
		Rot180EdgeSymmetry() {
			super(SymmetriesConstraint.this.getGeneralModel().getNbCrowns());
		}

		public Coords from(Coords point) {
			return rot180edge(point);
		}
	}
}
