package view.patterns;

import generator.patterns.PatternLabel;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import utils.Couple;

import java.util.ArrayList;

public class PatternHexagon extends Polygon {

	private final Color[] COLORS = new Color[] { Color.WHITE, Color.GREEN, Color.RED, Color.ORANGE };

	private final PatternGroup group;

	private final Couple<Integer, Integer> coords;
	private final ArrayList<Double> points;

	private PatternLabel label;
	private boolean isCenter;

	PatternHexagon(PatternGroup group, Couple<Integer, Integer> coords, ArrayList<Double> points) {
		super();
		this.group = group;
		this.coords = coords;
		this.points = points;
		label = PatternLabel.VOID;
		isCenter = false;

		initialize();
	}

	private void initialize() {

		this.setOnMouseClicked(e -> {
			System.out.println(coords.toString());
			shiftLabel();
			group.getParentPane().checkBorder();
		});

		this.getPoints().addAll(points);

		this.setFill(Color.WHITE);
		this.setStroke(Color.BLACK);
	}

	public PatternLabel getLabel() {
		return label;
	}

	private void shiftLabel() {
    PatternLabel lastLabel = group.getParentPane().getLastLabel();
    if (lastLabel == label) {
      label = PatternLabel.next(label);
      group.getParentPane().setLastLabel(label);
    }
    else
      label = lastLabel;
	this.setFill(COLORS[label.ordinal()]);
	}

	public void setLabel(PatternLabel label) {
		this.label = label;
		this.setFill(COLORS[label.ordinal()]);
	}

	void disableCenter() {
		isCenter = false;
		this.setStrokeWidth(this.getStrokeWidth() / 2);
	}

	public boolean isCenter() {
		return isCenter;
	}

	public Couple<Integer, Integer> getCoords() {
		return coords;
	}

	void setBorderAction() {
		this.setOnMouseClicked(e -> {
			System.out.println("Click on border");
			shiftLabel();
			group.getParentPane().addCrown();
		});
	}

	@Override
	public String toString() {
		return coords.toString();
	}
}
