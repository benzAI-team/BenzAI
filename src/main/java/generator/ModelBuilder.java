package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.fragments.FragmentResolutionInformations;
import modules.CatacondensedModule;
import modules.CatacondensedModule2;
import modules.CoronenoidModule;
import modules.CoronoidModule;
import modules.CoronoidModule2;
import modules.DiameterModule;
import modules.ForbiddenFragmentModule1;
import modules.IrregularityModule;
import modules.MultipleFragments1Module;
import modules.NbCarbonsModule;
import modules.NbHydrogensModule;
import modules.RectangleModule;
import modules.RhombusModule;
import modules.SingleFragment1Module;
import modules.SymmetriesModule;
import solving_modes.SymmetryType;
import utils.Utils;

public class ModelBuilder {

	public static ArrayList<GeneralModel> buildModel(ArrayList<GeneratorCriterion> criterions,
			Map<String, ArrayList<GeneratorCriterion>> map, FragmentResolutionInformations patternsInformations) {

		ArrayList<GeneralModel> models = new ArrayList<>();

		if (map.get("hexagons").size() == 0 && map.get("carbons").size() == 0 && map.get("hydrogens").size() == 0
				&& map.get("coronenoid").size() == 0 && map.get("rectangle").size() == 0
				&& map.get("rhombus").size() == 0)
			return null;

		int upperBoundHexagons = Integer.MAX_VALUE;

		for (GeneratorCriterion criterion : map.get("hexagons")) {

			Operator operator = criterion.getOperator();
			int value = Integer.parseInt(criterion.getValue());

			if (operator == Operator.EQ || operator == Operator.LEQ || operator == Operator.LT) {

				if (operator == Operator.LT)
					value--;

				if (value < upperBoundHexagons)
					upperBoundHexagons = value;
			}
		}

		for (GeneratorCriterion criterion : map.get("carbons")) {

			Operator operator = criterion.getOperator();

			if (operator == Operator.EQ || operator == Operator.LEQ || operator == Operator.LT) {

				double nbAtoms = Double.parseDouble(criterion.getValue());

				if (operator == Operator.LT)
					nbAtoms--;

				double bound = Math.ceil(((nbAtoms - 6.0) / 4.0) + 1.0);
				if (bound < upperBoundHexagons)
					upperBoundHexagons = (int) bound;

			}
		}

		for (GeneratorCriterion criterion : map.get("hydrogens")) {

			Operator operator = criterion.getOperator();

			if (operator == Operator.EQ || operator == Operator.LEQ || operator == Operator.LT) {

				double nbAtoms = Double.parseDouble(criterion.getValue());

				if (operator == Operator.LT)
					nbAtoms--;

				double bound = Math.ceil(((nbAtoms - 8) / 2.0) + 2.0);
				if (bound < upperBoundHexagons)
					upperBoundHexagons = (int) bound;

			}
		}

		for (GeneratorCriterion criterion : map.get("coronenoid")) {

			Operator operator = criterion.getOperator();
			Subject subject = criterion.getSubject();

			if (subject == Subject.NB_CROWNS
					&& (operator == Operator.EQ || operator == Operator.LEQ || operator == Operator.LT)) {

				int value = Integer.parseInt(criterion.getValue());

				double bound = 6.0 * (((double) value * ((double) value - 1.0)) / 2.0) + 1.0;

				if (bound < upperBoundHexagons)
					upperBoundHexagons = (int) bound;
			}
		}

		int nbMaxHeight = 0;
		int nbMaxWidth = 0;

		ArrayList<GeneratorCriterion> criterionsHeight = new ArrayList<>();
		ArrayList<GeneratorCriterion> criterionsWidth = new ArrayList<>();

		for (GeneratorCriterion criterion : map.get("rectangle")) {

			Operator operator = criterion.getOperator();
			Subject subject = criterion.getSubject();

			if (subject == Subject.RECT_HEIGHT) {

				criterionsHeight.add(criterion);

				if (operator == Operator.EQ || operator == Operator.LEQ || operator == Operator.LT) {

					int value = Integer.parseInt(criterion.getValue());

					if (value > nbMaxHeight)
						nbMaxHeight = value;
				}
			}

			if (subject == Subject.RECT_WIDTH) {

				criterionsWidth.add(criterion);

				if (operator == Operator.EQ || operator == Operator.LEQ || operator == Operator.LT) {

					int value = Integer.parseInt(criterion.getValue());

					if (value > nbMaxWidth)
						nbMaxWidth = value;

				}
			}
		}

		if (nbMaxHeight > 0 && nbMaxWidth > 0) {
			int bound = nbMaxHeight * nbMaxWidth;
			if (bound < upperBoundHexagons) {
				upperBoundHexagons = bound;
			}
		}

		// ~ if (nbMaxHeight < nbMaxWidth) {
		// ~ for (GeneratorCriterion cri : criterionsHeight)
		// ~ cri.setSubject(Subject.RECT_WIDTH);
		// ~ for (GeneratorCriterion cri : criterionsWidth)
		// ~ cri.setSubject(Subject.RECT_HEIGHT);

		// ~ }

		for (GeneratorCriterion criterion : map.get("rhombus")) {

			Subject subject = criterion.getSubject();
			String value = criterion.getValue();

			if (subject == Subject.RHOMBUS_DIMENSION && Utils.isNumber(value) && criterion.isUpperBound()) {

				int intVal = Integer.parseInt(value);
				int bound = intVal * intVal;

				if (bound < upperBoundHexagons) {
					upperBoundHexagons = bound;
				}
			}
		}

		if (map.get("hexagons").size() == 0) {
			map.get("hexagons").add(
					new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.LEQ, Integer.toString(upperBoundHexagons)));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONENOID)) {

			int nbMaxCrowns = getNbCrownsMax(upperBoundHexagons);
			models.add(new GeneralModel(map.get("hexagons"), criterions, map, nbMaxCrowns));
		}

		else if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_60)
				|| GeneratorCriterion.containsSubject(criterions, Subject.ROT_60_MIRROR)) {

			int nbMaxHexagons = 0;

			for (GeneratorCriterion criterion : map.get("hexagons")) {

				Operator operator = criterion.getOperator();
				if (operator == Operator.EQ || operator == Operator.LT || operator == Operator.LEQ) {
					int value = Integer.parseInt(criterion.getValue());
					if (value > nbMaxHexagons)
						nbMaxHexagons = value;
				}
			}

			int nbMaxCrowns = (upperBoundHexagons + 10) / 6;

			for (int i = 2; i <= nbMaxCrowns; i++) {
				models.add(new GeneralModel(map.get("hexagons"), criterions, map, i));
			}
		}

		else if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120)
				|| GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120_V)
				|| GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_H)
				|| GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_E)
				|| GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_VERTEX_MIRROR)) {

			int nbMaxHexagons = 0;

			for (GeneratorCriterion criterion : map.get("hexagons")) {

				Operator operator = criterion.getOperator();
				if (operator == Operator.EQ || operator == Operator.LT || operator == Operator.LEQ) {
					int value = Integer.parseInt(criterion.getValue());
					if (value > nbMaxHexagons)
						nbMaxHexagons = value;
				}
			}

			int nbMaxCrowns = (upperBoundHexagons + 4) / 3;

			for (int i = 2; i <= nbMaxCrowns; i++) {
				models.add(new GeneralModel(map.get("hexagons"), criterions, map, i));
			}
		}

		else if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_VERTICAL)
				|| GeneratorCriterion.containsSubject(criterions, Subject.SYMM_MIRROR)
				|| GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180)
				|| GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180_E)
				|| GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_EDGE_MIRROR)
				|| GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_MIRROR)) {

			int nbMaxHexagons = 0;

			for (GeneratorCriterion criterion : map.get("hexagons")) {

				Operator operator = criterion.getOperator();
				if (operator == Operator.EQ || operator == Operator.LT || operator == Operator.LEQ) {
					int value = Integer.parseInt(criterion.getValue());
					if (value > nbMaxHexagons)
						nbMaxHexagons = value;
				}
			}

			int nbMaxCrowns = (int) Math.floor((((double) ((double) nbMaxHexagons + 1)) / 2.0) + 1.0);

//			for (int i = 2; i <= nbMaxCrowns; i++) {
//				models.add(new GeneralModel(map.get("hexagons"), criterions, map, i));
//			}

			models.add(new GeneralModel(map.get("hexagons"), criterions, map, nbMaxCrowns));

		}

		else if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID)
				|| GeneratorCriterion.containsSubject(criterions, Subject.CORONOID_2)
				|| GeneratorCriterion.containsSubject(criterions, Subject.NB_HOLES)) {

			int nbMaxHexagons = 0;

			for (GeneratorCriterion criterion : map.get("hexagons")) {

				Operator operator = criterion.getOperator();
				if (operator == Operator.EQ || operator == Operator.LT || operator == Operator.LEQ) {
					int value = Integer.parseInt(criterion.getValue());
					if (value > nbMaxHexagons)
						nbMaxHexagons = value;
				}
			}

			int nbMaxHoles = 0;

			for (GeneratorCriterion criterion : map.get("coronoid2")) {

				if (criterion.getSubject() == Subject.NB_HOLES) {

					Operator operator = criterion.getOperator();
					if (operator == Operator.EQ || operator == Operator.LT || operator == Operator.LEQ) {
						int value = Integer.parseInt(criterion.getValue());
						if (value > nbMaxHoles)
							nbMaxHoles = value;
					}

				}
			}

			if (nbMaxHoles == 0)
				nbMaxHoles = 1;

			int nbCrowns;

			if (nbMaxHexagons > 4 * nbMaxHoles)
				nbCrowns = (upperBoundHexagons + 2 - 4 * nbMaxHoles) / 2;

			else
				nbCrowns = 1;

			models.add(new GeneralModel(map.get("hexagons"), criterions, map, nbCrowns));
		}

		else {
			models.add(new GeneralModel(map.get("hexagons"), criterions, map));
		}

		for (GeneralModel model : models)
			model.setPatternsInformations(patternsInformations);

		if (map.get("carbons").size() > 0)
			for (GeneralModel model : models)
				model.addModule(new NbCarbonsModule(model, map.get("carbons")));

		if (map.get("hydrogens").size() > 0)
			for (GeneralModel model : models)
				model.addModule(new NbHydrogensModule(model, map.get("hydrogens")));

		if (map.get("coronenoid").size() > 0)
			for (GeneralModel model : models)
				model.addModule(new CoronenoidModule(model, map.get("coronenoid")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.VIEW_IRREG))
			for (GeneralModel model : models)
				model.addModule(new IrregularityModule(model, map.get("irregularity")));

		if (map.get("diameter").size() > 0)
			for (GeneralModel model : models)
				model.addModule(new DiameterModule(model, map.get("diameter")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.RECTANGLE))
			for (GeneralModel model : models)
				model.addModule(new RectangleModule(model, map.get("rectangle")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.RHOMBUS)) {
			for (GeneralModel model : models)
				model.addModule(new RhombusModule(model, map.get("rhombus")));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID))
			for (GeneralModel model : models)
				model.addModule(new CoronoidModule(model));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID_2))
			for (GeneralModel model : models)
				model.addModule(new CoronoidModule2(model, map.get("coronoid2")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CATACONDENSED))
			for (GeneralModel model : models)
				model.addModule(new CatacondensedModule2(model));

		/*
		 * Symmetries
		 */

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_60_MIRROR)) {
			for (GeneralModel model : models) {
				model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));
			}
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_VERTEX_MIRROR)) {
			for (GeneralModel model : models) {
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));
				model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
			}
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_H)) {
			for (GeneralModel model : models) {
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
				model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
			}
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_E)) {
			for (GeneralModel model : models) {
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
				model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
			}
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_EDGE_MIRROR)) {
			for (GeneralModel model : models) {
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));
				model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
			}
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_MIRROR)) {
			for (GeneralModel model : models) {
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));
				model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
			}
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_MIRROR))
			for (GeneralModel model : models)
				model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_60))
			for (GeneralModel model : models)
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120))
			for (GeneralModel model : models)
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180))
			for (GeneralModel model : models)
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_VERTICAL))
			for (GeneralModel model : models)
				model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120_V))
			for (GeneralModel model : models)
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180_E))
			for (GeneralModel model : models)
				model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));

		/*
		 * Patterns
		 */

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.SINGLE_PATTERN))
			for (GeneralModel model : models)
				model.addModule(new SingleFragment1Module(model, patternsInformations.getFragments().get(0),
						VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.MULTIPLE_PATTERNS))
			for (GeneralModel model : models)
				model.addModule(new MultipleFragments1Module(model, patternsInformations.getFragments(),
						VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST,
						patternsInformations.getInterraction()));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.FORBIDDEN_PATTERN))
			for (GeneralModel model : models)
				model.addModule(new ForbiddenFragmentModule1(model, patternsInformations.getFragments().get(0),
						VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		return models;
	}

	private static int getNbCrownsMax(int maxSize) {

		int nbCrowns = 1;

		while (true) {

			int nbHexagons = (int) (6.0 * ((nbCrowns * (nbCrowns - 1)) / 2.0) + 1.0);
			if (maxSize <= nbHexagons)
				break;

			nbCrowns++;
		}

		return nbCrowns;
	}

	public static GeneralModel buildModel(ArrayList<GeneratorCriterion> criterions,
			Map<String, ArrayList<GeneratorCriterion>> map, int nbCrowns,
			FragmentResolutionInformations patternsInformations) {

		GeneralModel model;

		model = new GeneralModel(map.get("hexagons"), criterions, map, nbCrowns);

		if (map.get("carbons_hydrogens").size() > 0)
			model.addModule(new NbCarbonsModule(model, map.get("carbons_hydrogens")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.VIEW_IRREG))
			model.addModule(new IrregularityModule(model, map.get("irregularity")));

		if (map.get("diameter").size() > 0)
			model.addModule(new DiameterModule(model, map.get("diameter")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.RECTANGLE))
			model.addModule(new RectangleModule(model, map.get("rectangle")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.RHOMBUS))
			model.addModule(new RhombusModule(model, map.get("rhombus")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID))
			model.addModule(new CoronoidModule(model));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID_2))
			model.addModule(new CoronoidModule2(model, map.get("coronoid2")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CATACONDENSED))
			model.addModule(new CatacondensedModule(model));

		/*
		 * Symmetries
		 */

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_60_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_VERTEX_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_H)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_E)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_EDGE_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_MIRROR))
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_60))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_VERTICAL))
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120_V))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180_E))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));

		/*
		 * Patterns
		 */

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.SINGLE_PATTERN))
			model.addModule(new SingleFragment1Module(model, patternsInformations.getFragments().get(0),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.MULTIPLE_PATTERNS))
			model.addModule(new MultipleFragments1Module(model, patternsInformations.getFragments(),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.FORBIDDEN_PATTERN))
			model.addModule(new ForbiddenFragmentModule1(model, patternsInformations.getFragments().get(0),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		return model;
	}

	public static GeneralModel buildModel(ArrayList<GeneratorCriterion> criterions,
			Map<String, ArrayList<GeneratorCriterion>> map, int nbCrowns) {

		GeneralModel model = new GeneralModel(map.get("hexagons"), criterions, map, nbCrowns);

		if (map.get("carbons_hydrogens").size() > 0)
			model.addModule(new NbCarbonsModule(model, map.get("carbons_hydrogens")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.VIEW_IRREG))
			model.addModule(new IrregularityModule(model, map.get("irregularity")));

		if (map.get("diameter").size() > 0)
			model.addModule(new DiameterModule(model, map.get("diameter")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.RECTANGLE))
			model.addModule(new RectangleModule(model, map.get("rectangle")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID))
			model.addModule(new CoronoidModule(model));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CATACONDENSED))
			model.addModule(new CatacondensedModule(model));

		/*
		 * Symmetries
		 */

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_60_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_VERTEX_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_H)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_E)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_EDGE_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_MIRROR))
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_60))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_VERTICAL))
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120_V))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180_E))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));

		return model;
	}

	public static GeneralModel buildModel(ArrayList<GeneratorCriterion> criterions) {

		Map<String, ArrayList<GeneratorCriterion>> map = new HashMap<>();

		map.put("hexagons", new ArrayList<>());
		map.put("carbons_hydrogens", new ArrayList<>());
		map.put("irregularity", new ArrayList<>());
		map.put("diameter", new ArrayList<>());
		map.put("rectangle", new ArrayList<>());
		map.put("rhombus", new ArrayList<>());
		map.put("coronoid", new ArrayList<>());
		map.put("coronoid2", new ArrayList<>());
		map.put("catacondensed", new ArrayList<>());
		map.put("symmetries", new ArrayList<>());
		map.put("patterns", new ArrayList<>());
		map.put("stop", new ArrayList<>());

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();

			if (subject == Subject.NB_HEXAGONS)
				map.get("hexagons").add(criterion);

			else if (subject == Subject.NB_CARBONS || subject == Subject.NB_HYDROGENS)
				map.get("carbons_hydrogens").add(criterion);

			else if (subject == Subject.XI || subject == Subject.N0 || subject == Subject.N1 || subject == Subject.N2
					|| subject == Subject.N3 || subject == Subject.N4)
				map.get("irregularity").add(criterion);

			else if (subject == Subject.RECT_HEIGHT || subject == Subject.RECT_WIDTH)
				map.get("rectangle").add(criterion);

			else if (subject == Subject.SYMM_MIRROR || subject == Subject.SYMM_ROT_60 || subject == Subject.SYMM_ROT_120
					|| subject == Subject.SYMM_ROT_180 || subject == Subject.SYMM_VERTICAL
					|| subject == Subject.SYMM_ROT_120_V || subject == Subject.SYMM_ROT_180_E
					|| subject == Subject.ROT_60_MIRROR || subject == Subject.ROT_120_MIRROR_H
					|| subject == Subject.ROT_120_MIRROR_E || subject == Subject.ROT_120_VERTEX_MIRROR
					|| subject == Subject.ROT_180_EDGE_MIRROR || subject == Subject.ROT_180_MIRROR)

				map.get("symmetries").add(criterion);

			else if (subject == Subject.DIAMETER)
				map.get("diameter").add(criterion);

			else if (subject == Subject.CORONOID)
				map.get("coronoid").add(criterion);

			else if (subject == Subject.CORONOID_2 || subject == Subject.NB_HOLES)
				map.get("coronoid2").add(criterion);

			else if (subject == Subject.CATACONDENSED)
				map.get("catacondensed").add(criterion);

			else if (subject == Subject.RHOMBUS)
				map.get("rhombus").add(criterion);

			else if (subject == Subject.TIMEOUT || subject == Subject.NB_SOLUTIONS)
				map.get("stop").add(criterion);
		}

		GeneralModel model = new GeneralModel(map.get("hexagons"), criterions, map);

		if (map.get("carbons_hydrogens").size() > 0)
			model.addModule(new NbCarbonsModule(model, map.get("carbons_hydrogens")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.VIEW_IRREG))
			model.addModule(new IrregularityModule(model, map.get("irregularity")));

		if (map.get("diameter").size() > 0)
			model.addModule(new DiameterModule(model, map.get("diameter")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.RECTANGLE))
			model.addModule(new RectangleModule(model, map.get("rectangle")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.RHOMBUS))
			model.addModule(new RhombusModule(model, map.get("rhombus")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID))
			model.addModule(new CoronoidModule(model));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CORONOID_2))
			model.addModule(new CoronoidModule2(model, map.get("coronoid2")));

		if (GeneratorCriterion.containsSubject(criterions, Subject.CATACONDENSED))
			model.addModule(new CatacondensedModule(model));

		/*
		 * Symmetries
		 */

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_60_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_VERTEX_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_H)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_120_MIRROR_E)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_EDGE_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.ROT_180_MIRROR)) {
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));
		}

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_MIRROR))
			model.addModule(new SymmetriesModule(model, SymmetryType.MIRROR));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_60))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_60));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_VERTICAL))
			model.addModule(new SymmetriesModule(model, SymmetryType.VERTICAL));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_120_V))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_120_VERTEX));

		if (GeneratorCriterion.containsSubject(criterions, Subject.SYMM_ROT_180_E))
			model.addModule(new SymmetriesModule(model, SymmetryType.ROT_180_EDGE));

		return model;
	}
}
