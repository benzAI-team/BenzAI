package view.draw;

import generator.patterns.PatternLabel;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import utils.Couple;

import java.util.ArrayList;

public class HexagonDraw extends Polygon {

	private static final Color[] COLORS = new Color[] { Color.WHITE, Color.DARKGRAY };
	private static final PatternLabel[] LABELS = new PatternLabel[] {PatternLabel.VOID, PatternLabel.NEUTRAL};

	private int index = 0;

	private final MoleculeGroup group;

	private final Couple<Integer, Integer> coordinates;
	private final ArrayList<Double> points;

	private PatternLabel label;
	private boolean isCenter;

	public HexagonDraw(MoleculeGroup group, Couple<Integer, Integer> coordinates, ArrayList<Double> points) {
		super();
		this.group = group;
		this.coordinates = coordinates;
		this.points = points;
		label = PatternLabel.VOID;
		isCenter = false;

		initialize();
	}

	private void initialize() {

		this.getPoints().addAll(points);

		this.setFill(Color.WHITE);
		this.setStroke(Color.BLACK);

		this.setOnMouseClicked(e -> {
			System.out.println(coordinates.toString());
			shiftLabel();
			group.getDrawPane().checkBorder();
		});

	}

	public void setBorderAction() {
		this.setOnMouseClicked(e -> {
			System.out.println(coordinates.toString() + "[Border]");
			shiftLabel();
			group.getDrawPane().addCrown();
		});
	}

	public PatternLabel getLabel() {
		return label;
	}

	private void shiftLabel() {
		index = 1 - index;
		label = LABELS[index];
		refreshColor();
	}

	public void setLabel(PatternLabel label) {
		this.label = label;
		if (label == PatternLabel.VOID)
			index = 0;
		else index = 1;
		refreshColor();
	}

	private void refreshColor() {
		Color color = COLORS[index];
		this.setFill(color);
	}

	public void disableCenter() {
		isCenter = false;
		this.setStrokeWidth(this.getStrokeWidth() / 2);
	}

	public boolean isCenter() {
		return isCenter;
	}

	public Couple<Integer, Integer> getCoordinates() {
		return coordinates;
	}

	@Override
	public String toString() {
		return coordinates.toString();
	}

}
