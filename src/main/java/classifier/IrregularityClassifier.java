package classifier;

import benzenoid.Benzenoid;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class IrregularityClassifier extends Classifier {

	private final double step;

	public IrregularityClassifier(HashMap<String, MoleculeInformation> moleculesInformations, double step)
			throws IOException {
		super(moleculesInformations);
		this.step = step;
	}

	public static Irregularity computeParameterOfIrregularity(Benzenoid molecule) {
		return molecule.getIrregularity();
	}

	@Override
	public ArrayList<PAHClass> classify() {

		int nbClasses = (int) (1.0 / step);

		System.out.println("step = " + step);
		System.out.println("nbClasses = " + nbClasses);

		ArrayList<PAHClass> classes = new ArrayList<>();

		double xiMin = 0;
		double xiMax = step;

		double[] steps = new double[nbClasses];

		for (int i = 0; i < nbClasses; i++) {

			BigDecimal bdXiMin = new BigDecimal(xiMin).setScale(1, RoundingMode.HALF_UP);

			BigDecimal bdXiMax;

			if (i < nbClasses - 1)
				bdXiMax = new BigDecimal(xiMax).setScale(1, RoundingMode.HALF_UP);
			else
				bdXiMax = new BigDecimal("1.0").setScale(1, RoundingMode.HALF_UP);

			String title;
			if (i == 0)
				title = "irregularity <= " + step;
			else
				title = "irregularity (> " + bdXiMin.doubleValue() + " AND <= " + bdXiMax.doubleValue() + ")";

			PAHClass PAHClass = new PAHClass(title, moleculesInformations);
			classes.add(PAHClass);

			steps[i] = bdXiMax.doubleValue();

			xiMin += step;
			xiMax += step;
		}

		for (Entry pair : moleculesInformations.entrySet()) {

			MoleculeInformation moleculeInformation = (MoleculeInformation) pair.getValue();

			String moleculeName = moleculeInformation.getMoleculeName();
			Benzenoid molecule = moleculeInformation.getMolecule();

			Irregularity irregularity = computeParameterOfIrregularity(molecule);

			if (irregularity != null) {

				double XI = irregularity.getXI();

				int index;

				for (int j = 0; j < nbClasses; j++) {
					if (XI <= steps[j]) {
						index = j;
						classes.get(index).addMolecule(moleculeName);
						break;
					}
				}
			}
		}
		/*
		 * for (int i = 0 ; i < classes.size() ; i++) { if
		 * (classes.get(i).getMoleculesNames().size() == 0) { classes.remove(i); i --; }
		 * }
		 */
		return classes;
	}
}
