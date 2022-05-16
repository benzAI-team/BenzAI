package view.filtering.criterions;

import java.util.ArrayList;
import java.util.HashMap;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.ModelBuilder;
import generator.ResultSolver;
import modules.BenzenoidModule;
import molecules.Molecule;
import view.generator.GeneratorPane;

public class SymmetriesCriterion extends FilteringCriterion {

	public String symmetry;

	public SymmetriesCriterion(String symmetry) {
		super();
		this.symmetry = symmetry;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
				Integer.toString(molecule.getNbHexagons()));

		criterions.add(hexagonCriterion);

		if (symmetry.equals("C_2v(a) \"face-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.SYMM_MIRROR, Operator.NONE, ""));

		else if (symmetry.equals("C_6h \"(face)-60-rotation\""))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_60, Operator.NONE, ""));

		else if (symmetry.equals("C_3h(i) \"face-120-rotation\""))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120, Operator.NONE, ""));

		else if (symmetry.equals("C_2h(i) \"vertex_180-rotation\""))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180, Operator.NONE, ""));

		else if (symmetry.equals("C_2v(b) \"edge-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.SYMM_VERTICAL, Operator.NONE, ""));

		else if (symmetry.equals("C_3h(ii) \"vertex-120-rotation\""))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120_V, Operator.NONE, ""));

		else if (symmetry.equals("C_2h(ii) \"edge-180-rotation\""))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180_E, Operator.NONE, ""));

		else if (symmetry.equals("D_6h \"(vertex)-60-rotation+(edge)-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.ROT_60_MIRROR, Operator.NONE, ""));

		else if (symmetry.equals("D_3h(ii) \"vertex-120-rotation+(edge)-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_VERTEX_MIRROR, Operator.NONE, ""));

		else if (symmetry.equals("D_3h(ia) \"face-120-rotation+face-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_H, Operator.NONE, ""));

		else if (symmetry.equals("D_3h(ib) \"face-120-rotation+edge-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_E, Operator.NONE, ""));

		else if (symmetry.equals("D_2h(ii) \"edge-180-rotation+edge-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.ROT_180_EDGE_MIRROR, Operator.NONE, ""));

		else if (symmetry.equals("D_2h(i) \"face-180-rotation+edge-mirror\""))
			criterions.add(new GeneratorCriterion(Subject.ROT_180_MIRROR, Operator.NONE, ""));

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		ResultSolver finalResultSolver = new ResultSolver();

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null);

		// int nbCrowns = molecule.getNbCrowns();
		int nbCrowns = model.getNbCrowns();
		if (nbCrowns > -1) {
			model.addModule(new BenzenoidModule(model, molecule));
			ResultSolver resultSolver = model.solve();
			finalResultSolver.addResult(resultSolver);
		}

		else {

			model.addModule(new BenzenoidModule(model, molecule));
			ResultSolver resultSolver = model.solve();

			finalResultSolver.addResult(resultSolver);

		}

		return finalResultSolver.size() > 0;
	}

	@Override
	public String toString() {
		return "SymmetriesCriterion";
	}
}
