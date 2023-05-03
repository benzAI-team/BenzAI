package generator;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.variables.BoolVar;

import constraints.Permutation;
import utils.Coords;

public enum OldLexLead {
	;

	public static int xy2i(int x, int y, GeneralModel model) {
		return x + y * model.getDiameter();
	}

	public static boolean inCoronenoid(int x, int y, GeneralModel generalModel) {
		if (x < 0 || y < 0 || x >= generalModel.getDiameter() || y >= generalModel.getDiameter())
			return false;
		if (y < generalModel.getNbCrowns())
			return x < generalModel.getNbCrowns() + y;
		else
			return x > y - generalModel.getNbCrowns();
	}

	public static boolean inCoronenoid(int i, GeneralModel generalModel) {
		return inCoronenoid(i % generalModel.getDiameter(), i / generalModel.getDiameter(), generalModel);
	}

	public static void postSymmetryBreakingConstraints(Permutation p, BoolVar y, GeneralModel generalModel) {
		int i, j;

		int largeur = generalModel.getDiameter();
		Model model = generalModel.getProblem();

		BoolVar[] benzenoidVertices = generalModel.getBenzenoidVerticesBVArray();
		BoolVar yp1 = model.boolVar();
		model.addClauses(LogOp.or(y));
		for (j = 0; j < largeur; j++)
			for (i = 0; i < largeur; i++)
				if (inCoronenoid(i, j, generalModel) && inCoronenoid(p.from(xy2i(i, j, generalModel)), generalModel)) {
					// System.out.println("-" + y.getName() + " " + xy2i(i,j) + " -" +
					// p.from(xy2i(i,j)));
					model.addClauses(LogOp.or(LogOp.nor(y), benzenoidVertices[xy2i(i, j, generalModel)],
							LogOp.nor(benzenoidVertices[p.from(xy2i(i, j, generalModel))])));
					if (j != largeur - 1 || i != largeur - 1) {
						// System.out.println((y.getName()+1) + " -" + y.getName() + " " + xy2i(i,j));
						model.addClauses(LogOp.or(yp1, LogOp.nor(y), benzenoidVertices[xy2i(i, j, generalModel)]));
						// System.out.println((y.getName()+1) + " -" + y.getName() + " -" +
						// p.from(xy2i(i,j)));
						model.addClauses(LogOp.or(yp1, LogOp.nor(y),
								LogOp.nor(benzenoidVertices[p.from(xy2i(i, j, generalModel))])));
					}
					y = yp1;
					yp1 = model.boolVar();
				}
	}

	/***
	 * Poste les 11 contraintes d'élimination de symétries : 6 rotations avec ou
	 * sans symétrie diagonale
	 */
	@SuppressWarnings("unused")
	public static void postSymmetryBreakingConstraints(GeneralModel generalModel) {
		int i, j;

		BoolVar y = generalModel.getProblem().boolVar();
		postSymmetryBreakingConstraints(new Permutation(generalModel.getNbCrowns(), 0) {
			@Override
			public Coords from(Coords indice) {
				return hexAxis(indice);
			}
		}, y, generalModel);

		for (i = 1; i < 6; i++) {
			postSymmetryBreakingConstraints(new Permutation(generalModel.getNbCrowns(), i) {
				@Override
				public Coords from(Coords indice) {
					return rot(indice);
				}
			}, y, generalModel);
			postSymmetryBreakingConstraints(new Permutation(generalModel.getNbCrowns(), i) {
				@Override
				public Coords from(Coords indice) {
					return hexAxis(rot(indice));
				}
			}, y, generalModel);
		}
	}
}
