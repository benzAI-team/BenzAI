package view.groups;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import molecules.Molecule;
import solution.ClarCoverSolution;

public class RadicalarClarCoverGroup extends MoleculeGroup {

	private ArrayList<ClarCoverSolution> clarCoverSolutions;

	public RadicalarClarCoverGroup(Molecule molecule, ArrayList<ClarCoverSolution> clarCoverSolutions) {
		super(molecule);
		this.clarCoverSolutions = clarCoverSolutions;
		drawRadicalar();
	}

	private void drawRadicalar() {

		if (clarCoverSolutions.size() > 0) {

			int nbCarbons = clarCoverSolutions.get(0).getNbCarbons();
			double[] radicalarStats = ClarCoverSolution.getRadicalarStatistics(clarCoverSolutions);

			for (int i = 0; i < nbCarbons; i++) {

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
					Circle circleShape = new Circle(x, y, stat * 10.0);
					circleShape.setStroke(Color.BLACK);
					circleShape.setFill(Color.TRANSPARENT);
					this.getChildren().add(circleShape);
				}
			}
		}
	}

}
