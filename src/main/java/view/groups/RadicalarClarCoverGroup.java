package view.groups;

import java.util.ArrayList;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import molecules.Molecule;
import solution.ClarCoverSolution;

public class RadicalarClarCoverGroup extends MoleculeGroup {

	private final ArrayList<ClarCoverSolution> clarCoverSolutions;

	public RadicalarClarCoverGroup(Molecule molecule) {
		super(molecule);
		this.clarCoverSolutions = molecule.getClarCoverSolutions();
		drawRadicalar();
	}

	private void drawRadicalar() {

		if (clarCoverSolutions.size() > 0) {

			int nbCarbons = clarCoverSolutions.get(0).getNbCarbons();
			double[] radicalarStats = ClarCoverSolution.getRadicalarStatistics(clarCoverSolutions);

			for (int i = 0; i < nbCarbons; i++) {

				assert radicalarStats != null;
				double stat = radicalarStats[i];

				if (stat > 0.0) {

					int hexagonIndex = molecule.getHexagonsInvolved(i).get(0);
					int[] hexagon = molecule.getHexagon(hexagonIndex);
					int position = -1;
					for (int j = 0; j < 6; j++) {
						if (hexagon[j] == i) {
							position = j;
							break;
						}
					}

					double x = hexagons[hexagonIndex].getPoints().get(2 * position);
					double y = hexagons[hexagonIndex].getPoints().get(2 * position + 1);
					Circle circleShape = new Circle(x, y, getRadius(stat));
					circleShape.setStroke(Color.RED);
					circleShape.setFill(Color.RED);
					circleShape.setStrokeWidth(2.0);
					Tooltip.install(circleShape, new Tooltip(Double.toString(stat)));
					this.getChildren().add(circleShape);
				}
			}
		}
	}

	private double getRadius(double coeff) {
		if (0.0 <= coeff && coeff <= 1.0)
			return 2.0 + Math.floor(coeff * 10.0);
		return -1.0;
	}
}
