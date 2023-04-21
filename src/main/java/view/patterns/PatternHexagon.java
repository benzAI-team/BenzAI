package view.patterns;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import utils.Couple;

public class PatternHexagon extends Polygon {

	private final Color[] COLORS = new Color[] { Color.WHITE, Color.GREEN, Color.ORANGE, Color.RED };

	private final PatternGroup group;

	private final Couple<Integer, Integer> coords;
	private final ArrayList<Double> points;

	private int label;
	private boolean isCenter;

	public PatternHexagon(PatternGroup group, Couple<Integer, Integer> coords, ArrayList<Double> points) {
		super();
		this.group = group;
		this.coords = coords;
		this.points = points;
		label = 0;
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

	public int getLabel() {
		return label;
	}

	private void shiftLabel() {
    int lastLabel = group.getParentPane().getColorLabel();
    
    if (lastLabel == label) {
      label = (label + 1) % 4;
      group.getParentPane().setColorLabel(label);
    }
    else
      label = lastLabel;
		this.setFill(COLORS[label]);
	}

	public void setLabel(int label) {
		this.label = label % 4;
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

	public void setBorderAction() {
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
