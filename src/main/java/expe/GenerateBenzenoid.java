package expe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.ModelBuilder;

public class GenerateBenzenoid {

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++)
			System.out.println(args[i] + " ");

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		for (int i = 0; i < args.length; i++) {

			if (args[i].equals("hexa")) {
				i++;
				criterions.add(new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ, args[i]));
			}

			if (args[i].equals("rect"))
				criterions.add(new GeneratorCriterion(Subject.RECTANGLE, Operator.NONE, ""));

			if (args[i].equals("coro"))
				criterions.add(new GeneratorCriterion(Subject.CORONOID, Operator.NONE, ""));

			if (args[i].equals("cata"))
				criterions.add(new GeneratorCriterion(Subject.CATACONDENSED, Operator.NONE, ""));

			if (args[i].equals("mirr"))
				criterions.add(new GeneratorCriterion(Subject.SYMM_MIRROR, Operator.NONE, ""));

			if (args[i].equals("rot60"))
				criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_60, Operator.NONE, ""));

			if (args[i].equals("rot120"))
				criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120, Operator.NONE, ""));

			if (args[i].equals("rot180"))
				criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180, Operator.NONE, ""));

			if (args[i].equals("vert"))
				criterions.add(new GeneratorCriterion(Subject.SYMM_VERTICAL, Operator.NONE, ""));

			if (args[i].equals("rot120v"))
				criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120_V, Operator.NONE, ""));

			if (args[i].equals("rot180e"))
				criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180_E, Operator.NONE, ""));

			if (args[i].equals("60mirror"))
				criterions.add(new GeneratorCriterion(Subject.ROT_60_MIRROR, Operator.NONE, ""));

			if (args[i].equals("120vertexmirror"))
				criterions.add(new GeneratorCriterion(Subject.ROT_120_VERTEX_MIRROR, Operator.NONE, ""));

			if (args[i].equals("120mirrorh"))
				criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_H, Operator.NONE, ""));

			if (args[i].equals("120mirrore"))
				criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_E, Operator.NONE, ""));

			if (args[i].equals("180emirror"))
				criterions.add(new GeneratorCriterion(Subject.ROT_180_EDGE_MIRROR, Operator.NONE, ""));

			if (args[i].equals("180mirror"))
				criterions.add(new GeneratorCriterion(Subject.ROT_180_MIRROR, Operator.NONE, ""));

			if (args[i].equals("180"))
				criterions.add(new GeneratorCriterion(Subject.ROT_60_MIRROR, Operator.NONE, ""));

			if (args[i].equals("xi")) {
				i++;
				criterions.add(new GeneratorCriterion(Subject.XI, Operator.EQ, args[i]));
			}

			if (args[i].equals("carb")) {
				i++;
				criterions.add(new GeneratorCriterion(Subject.NB_CARBONS, Operator.EQ, args[i]));
			}

			if (args[i].equals("hydr")) {
				i++;
				criterions.add(new GeneratorCriterion(Subject.NB_HYDROGENS, Operator.EQ, args[i]));
			}

			if (args[i].equals("diam")) {
				i++;
				criterions.add(new GeneratorCriterion(Subject.DIAMETER, Operator.EQ, args[i]));
			}

			if (args[i].equals("coro2")) {
				criterions.add(new GeneratorCriterion(Subject.CORONOID_2, Operator.NONE, ""));
			}

			if (args[i].equals("rhomb")) {
				criterions.add(new GeneratorCriterion(Subject.RHOMBUS, Operator.EQ, ""));
			}
		}

		System.out.println("criterions : ");
		for (GeneratorCriterion criterion : criterions)
			System.out.println(criterion.toString());

		// GeneralModel model = ModelBuilder.buildModel(criterions);

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

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();

			if (subject == Subject.NB_HEXAGONS)
				map.get("hexagons").add(criterion);

			else if (subject == Subject.NB_CARBONS || subject == Subject.NB_HYDROGENS)
				map.get("carbons_hydrogens").add(criterion);

			else if (subject == Subject.XI || subject == Subject.N0 || subject == Subject.N1 || subject == Subject.N2
					|| subject == Subject.N3 || subject == Subject.N4)
				map.get("irregularity").add(criterion);

			else if (subject == Subject.RECT_NB_LINES || subject == Subject.RECT_NB_COLUMNS)
				map.get("rectangle").add(criterion);

			else if (subject == Subject.RHOMBUS)
				map.get("rhombus").add(criterion);

			else if (subject == Subject.SYMM_MIRROR || subject == Subject.SYMM_ROT_60 || subject == Subject.SYMM_ROT_120
					|| subject == Subject.SYMM_ROT_180 || subject == Subject.SYMM_VERTICAL
					|| subject == Subject.SYMM_ROT_120_V || subject == Subject.SYMM_ROT_180_E)

				map.get("symmetries").add(criterion);

			else if (subject == Subject.DIAMETER)
				map.get("diameter").add(criterion);

			else if (subject == Subject.CORONOID)
				map.get("coronoid").add(criterion);

			else if (subject == Subject.CORONOID_2 || subject == Subject.NB_HOLES)
				map.get("coronoid2").add(criterion);

			else if (subject == Subject.CATACONDENSED)
				map.get("catacondensed").add(criterion);

			else if (subject == Subject.SINGLE_PATTERN || subject == Subject.MULTIPLE_PATTERNS
					|| subject == Subject.FORBIDDEN_PATTERN || subject == Subject.OCCURENCE_PATTERN)
				map.get("patterns").add(criterion);
		}

		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue().toString());
		}

		GeneralModel model = ModelBuilder.buildModel(criterions, map, null).get(0);

		model.solve();
	}
}
