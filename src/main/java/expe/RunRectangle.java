package expe;

import java.util.ArrayList;
import java.util.HashMap;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.ResultSolver;
import modules.RectangleModule;
import modules.RectangleModule2;

public class RunRectangle {
	
	public static HashMap<String, ArrayList<GeneratorCriterion>> buildCriterionsMap(
			ArrayList<GeneratorCriterion> criterions) {

		HashMap<String, ArrayList<GeneratorCriterion>> map = new HashMap<>();

		map.put("hexagons", new ArrayList<>());
		map.put("carbons", new ArrayList<>());
		map.put("hydrogens", new ArrayList<>());
		map.put("coronenoid", new ArrayList<>());
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

			else if (subject == Subject.NB_CARBONS)
				map.get("carbons").add(criterion);

			else if (subject == Subject.NB_HYDROGENS)
				map.get("hydrogens").add(criterion);

			else if (subject == Subject.CORONENOID || subject == Subject.NB_CROWNS)
				map.get("coronenoid").add(criterion);

			else if (subject == Subject.XI || subject == Subject.N0 || subject == Subject.N1 || subject == Subject.N2
					|| subject == Subject.N3 || subject == Subject.N4)
				map.get("irregularity").add(criterion);

			else if (subject == Subject.RECT_HEIGHT || subject == Subject.RECT_WIDTH)
				map.get("rectangle").add(criterion);

			else if (subject == Subject.RHOMBUS || subject == Subject.RHOMBUS_DIMENSION)
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

			else if (subject == Subject.TIMEOUT || subject == Subject.NB_SOLUTIONS)
				map.get("stop").add(criterion);
		}

		return map;
	}
	
	private static void usage() {
		System.out.println("java -jar file.jar height width model");
	}
	
	public static void main(String [] args) {
		
		if (args.length != 2)
			usage();
		
		else {
			
//			int height = Integer.parseInt(args[0]);
//			int width = Integer.parseInt(args[1]);
		
			int height = 2;
			int width = 5;
			
			System.out.println("heigh = " + height + " width = " + width);
		
			String model = "2";
			
			int dimension = width * height;
			
			ArrayList<GeneratorCriterion> criterionsRectangle = new ArrayList<>();
			criterionsRectangle.add(new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.LEQ, Integer.toString(dimension)));
			criterionsRectangle.add(new GeneratorCriterion(Subject.RECTANGLE, Operator.NONE, ""));
			criterionsRectangle.add(new GeneratorCriterion(Subject.RECT_WIDTH, Operator.EQ, Integer.toString(width)));
			criterionsRectangle.add(new GeneratorCriterion(Subject.RECT_HEIGHT, Operator.LEQ, Integer.toString(height)));

			HashMap<String, ArrayList<GeneratorCriterion>> map = buildCriterionsMap(criterionsRectangle);
			
			int nbMaxCrowns = (int) Math.floor((((double) ((double) dimension + 1)) / 2.0) + 1.0);
			if (dimension % 2 == 1)
				nbMaxCrowns--;

			GeneralModel generalModel = new GeneralModel(map.get("hexagons"), criterionsRectangle, map, nbMaxCrowns);
			
			if (model.equals("1"))
				generalModel.addModule(new RectangleModule(generalModel, criterionsRectangle));
			else
				generalModel.addModule(new RectangleModule2(generalModel, criterionsRectangle));
			
			ResultSolver r = generalModel.solve();
			
			
		}
		
	}
}
