package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import generator.criterions.GeneratorCriterion2;
import generator.criterions.patterns.PatternGeneratorCriterion;
import generator.fragments.FragmentResolutionInformations;

public class ModelFactory {

	/*
	 * Solver's parameters
	 */

	private ArrayList<GeneratorCriterion2> criterions;
	private Map<String, ArrayList<GeneratorCriterion2>> criterionsMap;
	private FragmentResolutionInformations patternsInformations;

	/*
	 * Optimization variables
	 */

	private int upperBoundNbHexagons;
	private int upperBoundNbCrowns;

	public ModelFactory(ArrayList<GeneratorCriterion2> criterions) {
		this.criterions = criterions;
	}

	public GeneralModel buildModel() {

		buildMap();
		retrievePatternsInformations();
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

	private void buildMap() {

		criterionsMap = new HashMap<>();

		for (GeneratorCriterion2 criterion : criterions)
			criterion.buildMap(criterionsMap);
	}

	private void retrievePatternsInformations() {

		if (criterionsMap.get("patterns") != null) {
			for (GeneratorCriterion2 criterion : criterionsMap.get("patterns"))
				patternsInformations = ((PatternGeneratorCriterion) criterion).getPatternsInformations();
		} else
			patternsInformations = null;
	}
}
