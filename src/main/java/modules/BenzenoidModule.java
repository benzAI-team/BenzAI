package modules;

import java.util.ArrayList;

import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.fragments.Fragment;
import generator.fragments.FragmentOccurences;
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

		ArrayList<Integer> topBorder = new ArrayList<>();
		ArrayList<Integer> leftBorder = new ArrayList<>();

		for (int i = 0; i < diameter; i++) {
			if (coordsMatrix[0][i] != -1)
				topBorder.add(coordsMatrix[0][i]);
		}

		for (int i = 0; i < diameter; i++) {

			int j = 0;
			while (coordsMatrix[i][j] == -1)
				j++;

			leftBorder.add(coordsMatrix[i][j]);
		}

		ArrayList<Fragment> rotations = pattern.computeRotations();

		// boolean ok = true;

		FragmentOccurences occurences = new FragmentOccurences();

		for (Fragment f : rotations) {
			occurences.addAll(
					generalModel.computeTranslationsBorders(f.getNodesRefs(), f.getNeighborGraph(), occurences, false));
		}

		ArrayList<Integer[]> occurencesConstraints = new ArrayList<>();

		int indexOccurence = -1;
		Integer[] occurenceValid = null;
		for (Integer[] occurence : occurences.getOccurences()) {

			if (!occurencesConstraints.contains(occurence))
				occurencesConstraints.add(occurence);
 
			boolean contains = false;
			for (int i = 0; i < occurence.length; i++) {
				if (occurence[i] == 0) {
					indexOccurence = i;
					occurenceValid = occurence;
					contains = true;
					break;
				}
			}
			if (contains)
				break;
		}

//		Constraint[] constraints = new Constraint[occurences.size()];
//		for (int i = 0; i < occurences.size(); i++) {
//			Integer[] occurence = occurences.getOccurences().get(i);
//			IntVar[] variables = new IntVar[occurence.length];
//			for (int j = 0; j < variables.length; j++) {
//				variables[j] = generalModel.getVG()[occurence[j]];
//			}
//			constraints[i] = generalModel.getProblem().sum(variables, "=", molecule.getNbHexagons());
//		}
//		generalModel.getProblem().or(constraints).post();

		Integer[] occurence = occurences.getOccurences().get(0);
		IntVar[] variables = new IntVar[occurence.length];

		for (int i = 0; i < variables.length; i++) {
			variables[i] = generalModel.getVG()[occurence[i]];
		}

		generalModel.getProblem().sum(variables, "=", molecule.getNbHexagons()).post();
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

	private ArrayList<ArrayList<Integer>> translations(Fragment pattern, int diameter, int[][] coordsMatrix,
			ArrayList<Integer> topBorder, ArrayList<Integer> leftBorder) {

		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();

		ArrayList<String> names = new ArrayList<>();

		int xShiftMax = Math.max(Math.abs(pattern.xMax() - pattern.xMin()), diameter);
		int yShiftMax = Math.max(Math.abs(pattern.yMax() - pattern.yMin()), diameter);

		for (int xShift = 0; xShift < xShiftMax; xShift++) {
			for (int yShift = 0; yShift < yShiftMax; yShift++) {

				Node[] initialCoords = pattern.getNodesRefs();
				Node[] shiftedCoords = new Node[initialCoords.length];

				boolean ok = true;

				for (int i = 0; i < shiftedCoords.length; i++) {
					Node node = initialCoords[i];

					Node newNode = new Node(node.getX() + xShift, node.getY() + yShift, i);
					shiftedCoords[i] = newNode;

					int x = newNode.getX();
					int y = newNode.getY();

					if (x < 0 || x >= diameter || y < 0 || y >= diameter || coordsMatrix[x][y] == -1) {
						ok = false;
						break;
					}
				}

				if (ok) {
					ArrayList<Integer> hexagons = new ArrayList<>();
					for (int i = 0; i < shiftedCoords.length; i++) {
						Node node = shiftedCoords[i];
						int hexagon = coordsMatrix[node.getX()][node.getY()];
						hexagons.add(hexagon);
					}

					translations.add(hexagons);

//					if (touchBorder(hexagons, topBorder, leftBorder)) {
//						
//					}
				}
			}
		}

		return translations;
	}
}
