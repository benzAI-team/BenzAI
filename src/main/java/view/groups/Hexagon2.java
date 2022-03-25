package view.groups;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import utils.Couple;

public class Hexagon2 extends Polygon {

	private Couple<Integer, Integer> coords;
	private ArrayList<Double> points;
	private Couple<Double, Double> center;

	private int index;
	
	public Hexagon2(Couple<Integer, Integer> coords, ArrayList<Double> points, int index) {
		super();
		this.coords = coords;
		this.points = points;
		this.index = index;
		initialize();
	}

	private void initialize() {

		this.getPoints().addAll(points);
		this.setFill(Color.WHITE);
		this.setStroke(Color.BLACK);
	}

	public Couple<Integer, Integer> getCoords() {
		return coords;
	}

	public Couple<Double, Double> getCenter() {
		if (center != null)
			return center;

		double x1 = 0.0;
		double x2 = 0.0;

		for (int i = 0; i < points.size(); i += 2)
			x1 += points.get(i);

		for (int i = 1; i < points.size(); i += 2)
			x2 += points.get(i);

		x1 = x1 / 6;
		x2 = x2 / 6;

		center = new Couple<>(x1, x2);

		return center;
	}
}
