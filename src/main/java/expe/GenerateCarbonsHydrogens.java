package expe;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.ModelBuilder;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;

public class GenerateCarbonsHydrogens {

	public static void main(String [] args) {
		
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		criterions.add(new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.LEQ, "9"));
		criterions.add(new GeneratorCriterion(Subject.NB_CARBONS, Operator.LEQ, "34"));
		
		GeneralModel model = ModelBuilder.buildModel(criterions);
		model.solve();
	}
}
