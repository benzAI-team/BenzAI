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

public class CoronoidCriterion extends FilteringCriterion {

	private String operatorNbHoles;
	private int nbHoles;

	public CoronoidCriterion() {
		super();
	}

	public CoronoidCriterion(String operatorNbHoles, int nbHoles) {
		super();
		this.operatorNbHoles = operatorNbHoles;
		this.nbHoles = nbHoles;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		ResultSolver resultSolver = null;

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		if (operatorNbHoles == null) {

			GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
					Integer.toString(molecule.getNbHexagons()));

			criterions.add(hexagonCriterion);
			criterions.add(new GeneratorCriterion(Subject.CORONOID_2, Operator.NONE, ""));

			if (operatorNbHoles != null && nbHoles > 0) {
				Operator operator = GeneratorCriterion.getOperator(operatorNbHoles);
				criterions.add(new GeneratorCriterion(Subject.NB_HOLES, operator, Integer.toString(nbHoles)));
			}

		}

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null);

		model.addModule(new BenzenoidModule(model, molecule));

		resultSolver = model.solve();

		return resultSolver.size() > 0;
	}

	@Override
	public String toString() {
		return "CoronoidCriterion";
	}
}
