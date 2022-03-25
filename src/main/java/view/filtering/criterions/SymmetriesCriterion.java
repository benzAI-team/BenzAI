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

		if (symmetry.equals("Mirror symmetry"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_MIRROR, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 60°"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_60, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 120°"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 180°"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180, Operator.NONE, ""));

		else if (symmetry.equals("Vertical symmetry"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_VERTICAL, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 120° (vertex)"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120_V, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 180° (edges)"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180_E, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 60° + Mirror"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_60, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 120° (V) + Mirror"))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_VERTEX_MIRROR, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 120° + Mirror (H)"))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_H, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 120° + Mirror (E)"))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_E, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 180° + Mirror (E)"))
			criterions.add(new GeneratorCriterion(Subject.ROT_180_EDGE_MIRROR, Operator.NONE, ""));

		else if (symmetry.equals("Rotation of 180° + Mirror"))
			criterions.add(new GeneratorCriterion(Subject.ROT_180_MIRROR, Operator.NONE, ""));

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		ResultSolver finalResultSolver = new ResultSolver();

		ArrayList<GeneralModel> models = ModelBuilder.buildModel(criterions, criterionsMap, null);

		for (GeneralModel model : models) {
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
