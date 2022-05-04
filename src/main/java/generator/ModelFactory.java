package generator;

import java.util.ArrayList;
import java.util.Map;

import generator.criterions.GeneratorCriterion2;
import generator.fragments.FragmentResolutionInformations;

public class ModelFactory {

	/*
	 * Solver's parameters
	 */

	private ArrayList<GeneratorCriterion2> criterions;
	private Map<String, ArrayList<GeneratorCriterion2>> mapCriterions;
	private FragmentResolutionInformations patternsInformations;

	/*
	 * Optimization variables
	 */

	private int upperBoundNbHexagons;
	private int upperBoundNbCrowns;

	public ModelFactory(ArrayList<GeneratorCriterion2> criterions,
			Map<String, ArrayList<GeneratorCriterion2>> mapCriterions) {
		this.criterions = criterions;
		this.mapCriterions = mapCriterions;
	}

	public ModelFactory(ArrayList<GeneratorCriterion2> criterions,
			Map<String, ArrayList<GeneratorCriterion2>> mapCriterions,
			FragmentResolutionInformations patternsInformations) {
		this.criterions = criterions;
		this.mapCriterions = mapCriterions;
		this.patternsInformations = patternsInformations;
	}

	public GeneralModel buildModel() {

		optimizeNbHexagons();
		optimizeNbCrowns();

		if (upperBoundNbHexagons == -1)
			return null;

		return null;
	}

	private void optimizeNbHexagons() {

		upperBoundNbHexagons = -1;

		for (GeneratorCriterion2 criterion : criterions) {
			int nbHexagons = criterion.optimizeNbHexagons();
			if (nbHexagons != -1 && nbHexagons > upperBoundNbHexagons)
				upperBoundNbHexagons = nbHexagons;
		}
	}

	private void optimizeNbCrowns() {

		upperBoundNbCrowns = -1;

		for (GeneratorCriterion2 criterion : criterions) {
			int nbCrowns = criterion.optimizeNbCrowns(upperBoundNbHexagons);
			if (nbCrowns != -1 && nbCrowns > upperBoundNbCrowns)
				upperBoundNbCrowns = nbCrowns;
		}
	}

}
