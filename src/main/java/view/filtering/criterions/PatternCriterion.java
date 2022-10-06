package view.filtering.criterions;

import java.util.ArrayList;
import java.util.HashMap;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.patterns.PatternResolutionInformations;
import generator.ModelBuilder;
import generator.ResultSolver;
import modules.BenzenoidModule;
import molecules.Molecule;
import view.generator.GeneratorPane;

public class PatternCriterion extends FilteringCriterion {

	private GeneratorCriterion patternCriterion;
	private PatternResolutionInformations patternsInfos;

	public PatternCriterion(GeneratorCriterion patternCriterion, PatternResolutionInformations patternsInfos) {
		this.patternCriterion = patternCriterion;
		this.patternsInfos = patternsInfos;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
				Integer.toString(molecule.getNbHexagons()));

		criterions.add(hexagonCriterion);
		criterions.add(patternCriterion);

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, patternsInfos);

		model.addModule(new BenzenoidModule(model, molecule));

		if (molecule.toString() == "0") {
			System.out.print("");
		}

		System.out.println(molecule);
		ResultSolver resultSolver = model.solve();

		return resultSolver.size() > 0;
	}

	@Override
	public String toString() {
		return "PatternCriterion";
	}
}
