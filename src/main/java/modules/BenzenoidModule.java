package modules;

import java.util.ArrayList;

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

		ArrayList<Fragment> rotations = pattern.computeRotations();
		ArrayList<Integer[]> translations = new ArrayList<>();

		for (Fragment f : rotations)
			translations.addAll(translations(f));

		Constraint[] or = new Constraint[translations.size()];

		for (int i = 0; i < translations.size(); i++) {

			Integer[] translation = translations.get(i);
			IntVar[] variables = new IntVar[translation.length];

			for (int j = 0; j < translation.length; j++) {
				variables[j] = generalModel.getGraphVertices()[translation[j]];
			}

			or[i] = generalModel.getProblem().sum(variables, "=", variables.length);
		}

		if (or.length == 0)
			generalModel.getProblem().sum(generalModel.getChanneling(), "=", 0).post();
		else
			generalModel.getProblem().or(or).post();
	}

	@Override
	public void addVariables() {
		// DO_NOTHING
	}

	@Override
	public void changeSolvingStrategy() {
		// DO_NOTHING
	}

	@Override
	public void changeGraphVertices() {
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
}
