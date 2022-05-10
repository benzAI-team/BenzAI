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
					Circle circleShape = new Circle(x, y, getRadius(stat));
					circleShape.setStroke(Color.RED);
					circleShape.setFill(Color.RED);
					circleShape.setStrokeWidth(2.0);
					this.getChildren().add(circleShape);
				}
			}
		}
	}

	private double getRadius(double coeff) {
		
		if(0.0 >= coeff && coeff < 0.1)
			return 2.0;
		
		else if (0.1 >= coeff && coeff < 0.2)
			return 3.0;
		
		else if (0.2 >= coeff && coeff < 0.3)
			return 4.0;
		
		else if (0.3 >= coeff && coeff < 0.4)
			return 5.0;
		
		else if (0.4 >= coeff && coeff < 0.5)
			return 6.0;
		
		else if (0.5 >= coeff && coeff < 0.6)
			return 7.0;
		
		else if (0.6 >= coeff && coeff < 0.7)
			return 8.0;
		
		else if (0.7 >= coeff && coeff < 0.8)
			return 9.0;
		
		else if (0.8 >= coeff && coeff < 0.9)
			return 10.0;
		
		else if (0.9 >= coeff && coeff < 1.0)
			return 11.0;
		
		else if (coeff == 1.0)
			return 12.0;
		
		return -1.0;
	}
	
}
