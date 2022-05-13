package modules;

import java.util.ArrayList;
import java.util.Collections;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.fragments.Fragment;
import molecules.Molecule;
import molecules.Node;

public class BenzenoidModule extends Module {

	private Molecule molecule;

	public BenzenoidModule(GeneralModel generalModel, Molecule molecule) {
		super(generalModel);
		this.molecule = molecule;
	}

	@Override
	public void setPriority() {
		// DO_NOTHING
	}

	@Override
	public void buildVariables() {
		// DO_NOTHING
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postConstraints() {

		int diameter = generalModel.getDiameter();
		int[][] coordsMatrix = generalModel.getCoordsMatrix();

		Fragment pattern = molecule.convertToPattern(0, 0);

//		ArrayList<Integer> topBorder = new ArrayList<>();
//		ArrayList<Integer> leftBorder = new ArrayList<>();

//		for (int i = 0; i < diameter; i++) {
//			if (coordsMatrix[0][i] != -1)
//				topBorder.add(coordsMatrix[0][i]);
//		}
//
//		for (int i = 0; i < diameter; i++) {
//
//			int j = 0;
//			while (coordsMatrix[i][j] == -1)
//				j++;
//
//			leftBorder.add(coordsMatrix[i][j]);
//		}

		ArrayList<Fragment> rotations = pattern.computeRotations();
		ArrayList<Integer[]> translations = new ArrayList<>();

		for (Fragment f : rotations)
			translations.addAll(translations(f));

		Constraint[] or = new Constraint[translations.size()];

		for (int i = 0; i < translations.size(); i++) {

			Integer[] translation = translations.get(i);
			IntVar[] variables = new IntVar[translation.length];

			for (int j = 0; j < translation.length; j++) {
				variables[j] = generalModel.getWatchedGraphVertices()[translation[j]];
			}

			or[i] = generalModel.getProblem().sum(variables, "=", variables.length);
		}

		if (or.length == 0)
			generalModel.getProblem().sum(generalModel.getChanneling(), "=", 0).post();
		else
			generalModel.getProblem().or(or).post();
	}

	@Override
	public void addWatchedVariables() {
		// DO_NOTHING
	}

	@Override
	public void changeSolvingStrategy() {
		// DO_NOTHING
	}

	@Override
	public void changeWatchedGraphVertices() {
		// DO_NOTHING
	}

	private ArrayList<Integer[]> translations(Fragment pattern) {

		ArrayList<Integer[]> translations = new ArrayList<>();

		int diameter = generalModel.getDiameter();
		int[][] coordsMatrix = generalModel.getCoordsMatrix();

		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;

		for (Node node : pattern.getNodesRefs()) {
			if (node.getX() < xMin)
				xMin = node.getX();
			if (node.getY() < yMin)
				yMin = node.getY();
		}

		xMin = Math.abs(xMin);
		yMin = Math.abs(yMin);

		for (int xShift = xMin; xShift < diameter + xMin; xShift++) {
			for (int yShift = yMin; yShift < diameter + yMin; yShift++) {

				Integer[] translation = new Integer[pattern.getNbNodes()];
				boolean embedded = true;

				int i = 0;
				for (Node node : pattern.getNodesRefs()) {

					int x = node.getX() + xShift;
					int y = node.getY() + yShift;

					if (x >= diameter || y >= diameter) {
						embedded = false;
						break;
					}

					else if (coordsMatrix[x][y] == -1) {
						embedded = false;
						break;
					}

					int hexagonIndex = coordsMatrix[x][y];
					translation[i] = hexagonIndex;

					i++;
				}

				if (embedded)
					translations.add(translation);

			}
		}

		return translations;
	}
	
//	public ArrayList<ArrayList<Integer>> allTranslations() {
//
//		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();
//		int diameter = coordsMatrixCoronenoid.length;
//
//		ArrayList<ArrayList<Integer>> rotations = allRotations();
//
//		for (ArrayList<Integer> rotation : rotations) {
//
//			for (int xShift = -diameter; xShift <= diameter; xShift++) {
//				for (int yShift = -diameter; yShift <= diameter; yShift++) {
//
//					ArrayList<Integer> translation = new ArrayList<>();
//					boolean embedded = true;
//
//					for (int i = 0; i < rotation.size(); i++) {
//
//						int vertexIndex = rotation.get(i);
//
//						Node node = coronenoidNodes[vertexIndex];
//
//						int x = node.getX() + xShift;
//						int y = node.getY() + yShift;
//
//						if (!(x >= 0 && x < diameter && y >= 0 && y < diameter) || coordsMatrixCoronenoid[x][y] == -1) {
//							embedded = false;
//							break;
//						}
//
//						translation.add(correspondancesHexagons[coordsMatrixCoronenoid[x][y]]);
//					}
//
//					if (embedded) {
//						Collections.sort(translation);
//						if (!translations.contains(translation))
//							translations.add(translation);
//					}
//				}
//			}
//
//		}
//
//		return translations;
//
//	}
}
