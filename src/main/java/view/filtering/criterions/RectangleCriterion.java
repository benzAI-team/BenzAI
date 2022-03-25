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

public class RectangleCriterion extends FilteringCriterion {

	private String operatorNbLines;
	private int nbLines;

	private String operatorNbColumns;
	private int nbColumns;

	public RectangleCriterion() {
		super();
	}

	public RectangleCriterion(String operatorNbLines, int nbLines, String operatorNbColumns, int nbColumns) {
		super();
		this.operatorNbLines = operatorNbLines;
		this.nbLines = nbLines;
		this.operatorNbColumns = operatorNbColumns;
		this.nbColumns = nbColumns;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		ResultSolver resultSolver = null;

		if (operatorNbLines == null && operatorNbColumns == null) {

			ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
			GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
					Integer.toString(molecule.getNbHexagons()));

			criterions.add(hexagonCriterion);
			criterions.add(new GeneratorCriterion(Subject.RECTANGLE, Operator.NONE, ""));

			HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

			GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null).get(0);

			model.addModule(new BenzenoidModule(model, molecule));

			resultSolver = model.solve();

		}

		else {

			ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
			GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
					Integer.toString(molecule.getNbHexagons()));

			criterions.add(hexagonCriterion);
			criterions.add(new GeneratorCriterion(Subject.RECTANGLE, Operator.NONE, ""));

			if (operatorNbLines != null && nbLines > 0) {
				Operator operator = GeneratorCriterion.getOperator(operatorNbLines);
				criterions.add(new GeneratorCriterion(Subject.RECT_NB_LINES, operator, Integer.toString(nbLines)));
			}

			if (operatorNbColumns != null && nbColumns > 0) {
				Operator operator = GeneratorCriterion.getOperator(operatorNbColumns);
				criterions.add(new GeneratorCriterion(Subject.RECT_NB_COLUMNS, operator, Integer.toString(nbColumns)));
			}

			HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

			GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null).get(0);

			model.addModule(new BenzenoidModule(model, molecule));

			resultSolver = model.solve();

		}

		return resultSolver.size() > 0;
	}

	@Override
	public String toString() {
		return "RectangleCriterion";
	}
}
