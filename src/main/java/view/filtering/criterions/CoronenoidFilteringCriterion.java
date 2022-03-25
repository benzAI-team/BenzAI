package view.filtering.criterions;

import java.util.ArrayList;
import java.util.HashMap;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.ModelBuilder;
import generator.ResultSolver;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import modules.BenzenoidModule;
import molecules.Molecule;
import view.generator.GeneratorPane;

public class CoronenoidFilteringCriterion extends FilteringCriterion {

	private String operatorNbCrowns;
	private int nbCrowns;
	
	public CoronenoidFilteringCriterion() {
		super();
	}
	
	public CoronenoidFilteringCriterion(String operatorNbCrowns, int nbCrowns) {
		super();
		this.operatorNbCrowns = operatorNbCrowns;
		this.nbCrowns = nbCrowns;
	}
	
	@Override
	public Boolean checksCriterion(Molecule molecule) {
		ResultSolver resultSolver = null;
		
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		GeneratorCriterion hexagonCriterion = new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ,
				Integer.toString(molecule.getNbHexagons()));
		
		criterions.add(hexagonCriterion);
		criterions.add(new GeneratorCriterion(Subject.CORONENOID, Operator.NONE, ""));
		
		if (operatorNbCrowns != null) {
			Operator operator = GeneratorCriterion.getOperator(operatorNbCrowns);
			criterions.add(new GeneratorCriterion(Subject.NB_CROWNS, operator, Integer.toString(nbCrowns)));
		}
		
		
		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = GeneratorPane.buildCriterionsMap(criterions);

		GeneralModel model = ModelBuilder.buildModel(criterions, criterionsMap, null).get(0);

		model.addModule(new BenzenoidModule(model, molecule));

		resultSolver = model.solve();
		
		return resultSolver.size() > 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
