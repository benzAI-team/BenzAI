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

public class CatacondensedCriterion extends FilteringCriterion {

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
				Integer.toString(molecule.getNbHexagons()));

		criterions.add(hexagonCriterion);
		criterions.add(new GeneratorCriterion(Subject.CATACONDENSED, Operator.NONE, ""));

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null).get(0);

		model.addModule(new BenzenoidModule(model, molecule));

		ResultSolver resultSolver = model.solve();

		return resultSolver.size() > 0;
	}

	@Override
	public String toString() {
		return "CatacondensedCriterion";
	}

}
