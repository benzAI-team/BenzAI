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

public class RectangleCriterion extends FilteringCriterion {

	private String operatorHeight;
	private int height;

	private String operatorWidth;
	private int width;

	public RectangleCriterion() {
		super();
	}

	public RectangleCriterion(String operatorHeight, int height, String operatorWidth, int width) {
		super();
		this.operatorHeight = operatorHeight;
		this.height = height;
		this.operatorWidth = operatorWidth;
		this.width = width;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		SolverResults solverResults = null;

		if (operatorHeight == null && operatorWidth == null) {

			ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
			GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
					Integer.toString(molecule.getNbHexagons()));

			criterions.add(hexagonCriterion);
			criterions.add(new GeneratorCriterion(Subject.RECTANGLE, Operator.NONE, ""));

			HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

			GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null);

			model.addModule(new BenzenoidModule(model, molecule));

			solverResults = model.solve();

		}

		else {

			ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
			GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
					Integer.toString(molecule.getNbHexagons()));

			criterions.add(hexagonCriterion);
			criterions.add(new GeneratorCriterion(Subject.RECTANGLE, Operator.NONE, ""));

			if (operatorHeight != null && height > 0) {
				Operator operator = GeneratorCriterion.getOperator(operatorHeight);
				criterions.add(new GeneratorCriterion(Subject.RECT_HEIGHT, operator, Integer.toString(height)));
			}

			if (operatorWidth != null && width > 0) {
				Operator operator = GeneratorCriterion.getOperator(operatorWidth);
				criterions.add(new GeneratorCriterion(Subject.RECT_WIDTH, operator, Integer.toString(width)));
			}

			HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

			GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null);

			model.addModule(new BenzenoidModule(model, molecule));

			solverResults = model.solve();

		}

		return solverResults.size() > 0;
	}

	@Override
	public String toString() {
		return "RectangleCriterion";
	}
}
