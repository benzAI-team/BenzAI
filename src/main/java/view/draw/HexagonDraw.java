package view.draw;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import utils.Couple;

public class HexagonDraw extends Polygon {

	private final Color[] COLORS = new Color[] { Color.WHITE, Color.DARKGRAY };

	private MoleculeGroup group;

	private Couple<Integer, Integer> coords;
	private ArrayList<Double> points;

	private int label;
	private boolean isCenter;

	public HexagonDraw(MoleculeGroup group, Couple<Integer, Integer> coords, ArrayList<Double> points) {
		super();
		this.group = group;
		this.coords = coords;
		this.points = points;
		label = 0;
		isCenter = false;

		initialize();
	}

	private void initialize() {

		this.getPoints().addAll(points);

		this.setFill(Color.WHITE);
		this.setStroke(Color.BLACK);

		this.setOnMouseClicked(e -> {
			System.out.println(coords.toString());
			shiftLabel();
			group.getDrawPane().checkBorder();
		});

	}

	public void setBorderAction() {
		this.setOnMouseClicked(e -> {
			System.out.println("Click on border");
			shiftLabel();
			group.getDrawPane().addCrown();
		});
	}

	public int getLabel() {
		return label;
	}

	private void shiftLabel() {

		// ArrayList<HexagonDraw> neighbors = group.getNeighbors(coords);

		label = (label + 1) % COLORS.length;
		this.setFill(COLORS[label]);

//		if (label == 1) {
//			for (HexagonDraw neighbor : neighbors) {
//				if (neighbor.getLabel() == 0) {
//
//					ArrayList<HexagonDraw> neighbors2 = group.getNeighbors(neighbor.getCoords());
//					boolean surrounded = true;
//					for (HexagonDraw neighbor2 : neighbors2) {
//						if (neighbor2.getLabel() == 0) {
//							surrounded = false;
//							break;
//						}
//					}
//					if (surrounded) {
//						neighbor.setLabel(1);
//						neighbor.setFill(COLORS[1]);
//					}
//				}
//			}
//		}
//
//		else if (label == 0) {
//
//			boolean surrounded = true;
//			for (HexagonDraw neighbor : neighbors) {
//				if (neighbor.getLabel() == 0) {
//					surrounded = false;
//					break;
//				}
//			}
//			if (surrounded) {
//				this.setLabel(1);
//				this.setFill(COLORS[1]);
//			}
//		}
	}

	public void setLabel(int label) {

		this.label = label % COLORS.length;
		this.setFill(COLORS[label]);

	}

	public void disableCenter() {
		isCenter = false;
		this.setStrokeWidth(this.getStrokeWidth() / 2);

	}

	public boolean isCenter() {
		return isCenter;
	}

	public Couple<Integer, Integer> getCoords() {
		return coords;
	}

	@Override
	public String toString() {
		return coords.toString();
	}

}
