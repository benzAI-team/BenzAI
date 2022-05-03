package generator;

import java.util.ArrayList;
import java.util.Map;

import generator.fragments.FragmentResolutionInformations;

public class ModelFactory {

	/*
	 * Solver's parameters
	 */

	private ArrayList<GeneratorCriterion> criterions;
	private Map<String, ArrayList<GeneratorCriterion>> mapCriterions;
	private FragmentResolutionInformations patternsInformations;

	/*
	 * Optimization variables
	 */

	private int upperBoundNbHexagons;
	private int upperBoundNbCrowns;

	public ModelFactory(ArrayList<GeneratorCriterion> criterions,
			Map<String, ArrayList<GeneratorCriterion>> mapCriterions) {
		this.criterions = criterions;
		this.mapCriterions = mapCriterions;
	}

	public ModelFactory(ArrayList<GeneratorCriterion> criterions,
			Map<String, ArrayList<GeneratorCriterion>> mapCriterions,
			FragmentResolutionInformations patternsInformations) {
		this.criterions = criterions;
		this.mapCriterions = mapCriterions;
		this.patternsInformations = patternsInformations;
	}

	public GeneralModel buildModel() {

		return null;
	}

	private void optimizeNbHexagons() {

	}

	private void optimizeNbCrowns() {

	}

}
