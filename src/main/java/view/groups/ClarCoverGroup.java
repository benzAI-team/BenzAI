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

					int position1 = -1;
					int position2 = -1;

					for (int k = 0; k < 6; k++) {
						if (hexagon[k] == i)
							position1 = k;
						if (hexagon[k] == j)
							position2 = k;
					}

          if (position1 > position2)
          {
            int aux = position1;
            position1 = position2;
            position2 = aux;
          }
          
					double x1 = 0, y1 = 0;
					double x2 = 0, y2 = 0;

          switch (position1){
            case 0:
              x1 = hexagons[hexagonIndex].getPoints().get(0);
              y1 = hexagons[hexagonIndex].getPoints().get(1) + 5.0;
           
              if (position2 == 1)
              {
                x2 = hexagons[hexagonIndex].getPoints().get(2) - 4.0;
                y2 = hexagons[hexagonIndex].getPoints().get(3) + 2.5;
              }
              else
                {
                  x2 = hexagons[hexagonIndex].getPoints().get(10) + 4.0;
                  y2 = hexagons[hexagonIndex].getPoints().get(11) + 3.0;
                }
              break;
              
            case 1:
              x1 = hexagons[hexagonIndex].getPoints().get(2) - 4.0;
              y1 = hexagons[hexagonIndex].getPoints().get(3) + 2.0;
              
              x2 = hexagons[hexagonIndex].getPoints().get(4) - 4.0;
              y2 = hexagons[hexagonIndex].getPoints().get(5) - 2.0;
              break;
              
            case 2:
              x1 = hexagons[hexagonIndex].getPoints().get(4) - 4.0;
              y1 = hexagons[hexagonIndex].getPoints().get(5) - 2.5;
              
              x2 = hexagons[hexagonIndex].getPoints().get(6);
              y2 = hexagons[hexagonIndex].getPoints().get(7) - 5.0;
              
              break;
              
            case 3:
              x1 = hexagons[hexagonIndex].getPoints().get(6);
              y1 = hexagons[hexagonIndex].getPoints().get(7) - 5.0;
              
              x2 = hexagons[hexagonIndex].getPoints().get(8) + 4;
              y2 = hexagons[hexagonIndex].getPoints().get(9) - 2.5;
              
              break;
              
            case 4:
              x1 = hexagons[hexagonIndex].getPoints().get(8) + 4.0;
              y1 = hexagons[hexagonIndex].getPoints().get(9) - 2.0;
              
              x2 = hexagons[hexagonIndex].getPoints().get(10) + 4.0;
              y2 = hexagons[hexagonIndex].getPoints().get(11) + 2.0;
              
              break;
          
            case 5:
              break;
            }
          
          System.out.println("Coord ("+x1+","+y1+")");
          System.out.println("Coord ("+x2+","+y2+")");
          
					Line line = new Line(x1, y1, x2, y2);
					this.getChildren().add(line);
				}
			}
		}
	}

}
