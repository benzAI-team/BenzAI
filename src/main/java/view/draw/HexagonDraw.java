package view.draw;

import generator.patterns.PatternLabel;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import utils.Couple;

import java.util.ArrayList;

public class HexagonDraw extends Polygon {

	private final Color[] COLORS = new Color[] { Color.WHITE, Color.DARKGRAY };
	private final PatternLabel[] LABELS = new PatternLabel[] {PatternLabel.VOID, PatternLabel.NEUTRAL};

	private int index = 0;

	private final MoleculeGroup group;

	private final Couple<Integer, Integer> coords;
	private final ArrayList<Double> points;

	private PatternLabel label;
	private boolean isCenter;

	public HexagonDraw(MoleculeGroup group, Couple<Integer, Integer> coords, ArrayList<Double> points) {
		super();
		this.group = group;
		this.coords = coords;
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
			System.out.println(coords.toString());
			shiftLabel();
			group.getDrawPane().checkBorder();
		});

	}

	public void setBorderAction() {
		this.setOnMouseClicked(e -> {
			System.out.println(coords.toString() + "[Border]");
			shiftLabel();
			group.getDrawPane().addCrown();
		});
	}

	public PatternLabel getLabel() {
		return label;
	}

	private void shiftLabel() {
		/*
		label = PatternLabel.next(label);
		this.setFill(COLORS[label.ordinal()]);
		 */
		index = 1 - index;
		Color color = COLORS[index];
		label = LABELS[index];
		this.setFill(color);
	}

	public void setLabel(PatternLabel label) {
		//this.setFill(COLORS[label.ordinal()]);
		this.label = label;
		if (label == PatternLabel.VOID)
			index = 0;
		else index = 1;
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

	public Couple<Integer, Integer> getCoords() {
		return coords;
	}

	@Override
	public String toString() {
		return coords.toString();
	}

}
