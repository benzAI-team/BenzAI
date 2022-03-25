package view.groups;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import molecules.Molecule;
import solution.ClarCoverSolution;
import utils.Couple;

public class ClarCoverGroup extends MoleculeGroup {

	private ClarCoverSolution clarCoverSolution;

	public ClarCoverGroup(Molecule molecule, ClarCoverSolution clarCoverSolution) {
		super(molecule);
		removeTexts();
		this.clarCoverSolution = clarCoverSolution;

		drawCircles();
		drawSingleElectrons();
		drawBonds();
	}

	private void drawCircles() {
		for (int i = 0; i < clarCoverSolution.getNbHexagons(); i++) {

			if (clarCoverSolution.isCircle(i)) {
				Hexagon2 hexagon = hexagons[i];
				Couple<Double, Double> center = hexagon.getCenter();
				Circle circleShape = new Circle(center.getX(), center.getY(), 15.0);
				circleShape.setStroke(Color.BLACK);
				circleShape.setFill(Color.WHITE);
				this.getChildren().add(circleShape);
			}
		}

	}

	private void drawSingleElectrons() {
		for (int i = 0; i < clarCoverSolution.getNbCarbons(); i++) {

			if (clarCoverSolution.isSingle(i)) {

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
				Circle circleShape = new Circle(x, y, 5.0);
				circleShape.setStroke(Color.BLACK);
				circleShape.setFill(Color.TRANSPARENT);
				this.getChildren().add(circleShape);
			}
		}
	}

	private void drawBonds() {

		for (int i = 0; i < clarCoverSolution.getNbCarbons(); i++) {
			for (int j = (i + 1); j < clarCoverSolution.getNbCarbons(); j++) {
				if (clarCoverSolution.isDoubleBond(i, j)) {

					int hexagonIndex = molecule.getHexagonsInvolved(i, j).get(0);
					int[] hexagon = molecule.getHexagon(hexagonIndex);

					int iPosition = -1;
					int jPosition = -1;

					for (int k = 0; k < 6; k++) {
						if (hexagon[k] == i)
							iPosition = k;
						if (hexagon[k] == j)
							jPosition = k;
					}

					double x1, y1, x2, y2;
					double shift = 5.0;

					/*
					 * x1, y1
					 */

					if (iPosition == 0) {
						x1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition);
						y1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition + 1) + shift;
					}

					else if (iPosition == 1) {
						x1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition) - shift;
						y1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition + 1);
					}

					else if (iPosition == 2) {
						x1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition) - shift;
						y1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition + 1);
					}

					else if (iPosition == 3) {
						x1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition);
						y1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition + 1) - shift;
					}

					else if (iPosition == 4) {
						x1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition) + shift;
						y1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition + 1);
					}

					else {
						x1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition) + shift;
						y1 = hexagons[hexagonIndex].getPoints().get(2 * iPosition + 1);
					}

					/*
					 * x2, y2
					 */

					if (jPosition == 0) {
						x2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition);
						y2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition + 1) + shift;
					}

					else if (jPosition == 1) {
						x2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition) - shift;
						y2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition + 1);
					}

					else if (jPosition == 2) {
						x2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition) - shift;
						y2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition + 1);
					}

					else if (jPosition == 3) {
						x2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition);
						y2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition + 1) - shift;
					}

					else if (jPosition == 4) {
						x2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition) + shift;
						y2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition + 1);
					}

					else {
						x2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition) + shift;
						y2 = hexagons[hexagonIndex].getPoints().get(2 * jPosition + 1);
					}

					Line line = new Line(x1, y1, x2, y2);
					this.getChildren().add(line);
				}
			}
		}
	}

}
