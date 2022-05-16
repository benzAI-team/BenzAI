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

public class RhombusCriterion extends FilteringCriterion {

	private String operatorDimension;
	private int dimension;

	public RhombusCriterion() {
		super();
	}

	public RhombusCriterion(String operatorDimension, int dimension) {
		this.operatorDimension = operatorDimension;
		this.dimension = dimension;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
				Integer.toString(molecule.getNbHexagons()));

		criterions.add(hexagonCriterion);
		criterions.add(new GeneratorCriterion(Subject.RHOMBUS, Operator.NONE, ""));

		if (operatorDimension != null && dimension > 0) {
			Operator operator = GeneratorCriterion.getOperator(operatorDimension);
			criterions.add(new GeneratorCriterion(Subject.RECT_HEIGHT, operator, Integer.toString(dimension)));
		}

		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null);

		model.addModule(new BenzenoidModule(model, molecule));

		ResultSolver resultSolver = model.solve();

		return resultSolver.size() > 0;
	}

	@Override
	public String toString() {
		return "RhombusCriterion";
	}

}
