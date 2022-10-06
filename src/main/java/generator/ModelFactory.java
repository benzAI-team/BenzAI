package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import generator.criterions.GeneratorCriterion;
import generator.criterions.patterns.PatternGeneratorCriterion;
import generator.patterns.PatternResolutionInformations;

public class ModelFactory {

	/*
	 * Solver's parameters
	 */

	private ArrayList<GeneratorCriterion> criterions;
	private Map<String, ArrayList<GeneratorCriterion>> criterionsMap;
	private PatternResolutionInformations patternsInformations;

	/*
	 * Optimization variables
	 */

	private int upperBoundNbHexagons;
	private int upperBoundNbCrowns;

	public ModelFactory(ArrayList<GeneratorCriterion> criterions) {
		this.criterions = criterions;
	}

	public GeneralModel buildModel() {

		buildMap();
		retrievePatternsInformations();
		optimizeNbHexagons();

		if (upperBoundNbHexagons == -1)
			return null;

		optimizeNbCrowns();

		return null;
	}

	private void optimizeNbHexagons() {

		upperBoundNbHexagons = -1;

		for (GeneratorCriterion criterion : criterions) {
			int nbHexagons = criterion.optimizeNbHexagons();
			if (nbHexagons != -1 && nbHexagons > upperBoundNbHexagons)
				upperBoundNbHexagons = nbHexagons;
		}
	}

	private void optimizeNbCrowns() {

		upperBoundNbCrowns = -1;

		for (GeneratorCriterion criterion : criterions) {
			int nbCrowns = criterion.optimizeNbCrowns(upperBoundNbHexagons);
			if (nbCrowns != -1 && nbCrowns > upperBoundNbCrowns)
				upperBoundNbCrowns = nbCrowns;
		}

		if (upperBoundNbCrowns == -1) {
			upperBoundNbCrowns = (int) Math.floor((((double) ((double) upperBoundNbHexagons + 1)) / 2.0) + 1.0);

			if (upperBoundNbHexagons % 2 == 1)
				upperBoundNbCrowns--;
		}
	}

	private void buildMap() {

		criterionsMap = new HashMap<>();

		for (GeneratorCriterion criterion : criterions)
			criterion.buildMap(criterionsMap);
	}

	private void retrievePatternsInformations() {

		if (criterionsMap.get("patterns") != null) {
			for (GeneratorCriterion criterion : criterionsMap.get("patterns"))
				patternsInformations = ((PatternGeneratorCriterion) criterion).getPatternsInformations();
		} else
			patternsInformations = null;
	}

}
