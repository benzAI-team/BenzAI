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

public class DiameterCriterion extends FilteringCriterion {

	private String operatorDiameter;
	private int diameter;

	public DiameterCriterion(String operatorDiameter, int diameter) {
		this.operatorDiameter = operatorDiameter;
		this.diameter = diameter;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
				Integer.toString(molecule.getNbHexagons()));

		criterions.add(hexagonCriterion);

		Operator operator = GeneratorCriterion.getOperator(operatorDiameter);
		criterions.add(new GeneratorCriterion(Subject.DIAMETER, operator, Integer.toString(diameter)));

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null);

		model.addModule(new BenzenoidModule(model, molecule));

		SolverResults solverResults = model.solve();

		return solverResults.size() > 0;

	}

	@Override
	public String toString() {
		return "DiameterCriterion";
	}
}
