package view.filtering.criterions;

import java.util.ArrayList;
import java.util.HashMap;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.ModelBuilder;
import generator.SolverResults;
import modules.BenzenoidModule;
import molecules.Molecule;
import view.generator.GeneratorPane;

public class CatacondensedCriterion extends FilteringCriterion {

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		GeneratorCriterion hexagonCriterion = new GeneratorCriterion("NB_HEXAGONS", "=",
				molecule.getNbHexagons());

		criterions.add(hexagonCriterion);
		criterions.add(new GeneratorCriterion("CATACONDENSED", "", -1));

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null);

		model.addModule(new BenzenoidModule(model, molecule));

		SolverResults solverResults = model.solve();

		return solverResults.size() > 0;
	}

	@Override
	public String toString() {
		return "CatacondensedCriterion";
	}

}
