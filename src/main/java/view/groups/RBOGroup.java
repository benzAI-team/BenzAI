package view.groups;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import molecules.RBO;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import molecules.Benzenoid;
import view.collections.BenzenoidCollectionsManagerPane;

public class RBOGroup extends MoleculeGroup {

	@SuppressWarnings("unused")
	private BenzenoidCollectionsManagerPane pane;
	private final RBO RBO;

	private Color[] palette;

	public RBOGroup(Benzenoid molecule) {
		super(molecule);
		RBO = molecule.getRBO();
		removeTexts();
		buildPalette();
		writeRBO();
		coloringHexagons();
	}

	private void writeRBO() {

		for (int i = 0; i < hexagons.length; i++) {

			
			
			Hexagon2 hexagon = hexagons[i];
			BigDecimal bd = BigDecimal.valueOf(RBO.getRBO()[i]).setScale(2, RoundingMode.HALF_UP);

			Text text = new Text(Double.toString(bd.doubleValue()));
			text.setX(centersCoords[i].getX() + xShift - 15.0);
			text.setY(centersCoords[i].getY() + yShift + 5.0);
			text.setFill(Color.WHITE);
			text.setStroke(Color.WHITE);

			String tooltipStr = "H" + i + ": " + bd.doubleValue();
			Tooltip tooltip = new Tooltip(tooltipStr);

			Tooltip.install(hexagon, tooltip);
			Tooltip.install(text, tooltip);

			this.getChildren().add(text);
		}

	}

	private void coloringHexagons() {

		RBOResult rboResult = new RBOResult();

		for (int i = 0; i < RBO.getRBO().length; i++) {
			BigDecimal bd = BigDecimal.valueOf(RBO.getRBO()[i]).setScale(2, RoundingMode.HALF_UP);
			rboResult.add(bd.doubleValue(), i);
		}

		int nbColors = rboResult.size();
		int scale = Math.floorDiv(palette.length, nbColors);
		int colorIndex = 0;

		SortedSet<Double> keys = new TreeSet<>(rboResult.getMap().keySet());
		for (Double key : keys) {

			ArrayList<Integer> indexes = rboResult.getMap().get(key);
			Color color = palette[colorIndex];

			for (Integer index : indexes) {
				hexagons[index].setFill(color);
			}

			colorIndex += scale;
		}
	}

	private void buildPalette() {

		int[][] rgb = new int[][] { { 2, 3, 68 }, { 3, 9, 73 }, { 5, 15, 78 }, { 6, 22, 83 }, { 7, 28, 88 },
				{ 9, 34, 93 }, { 10, 40, 98 }, { 11, 47, 103 }, { 12, 53, 108 }, { 14, 59, 113 }, { 15, 65, 118 },
				{ 16, 72, 123 }, { 18, 78, 128 }, { 19, 84, 133 }, { 20, 90, 138 }, { 22, 97, 143 }, { 23, 103, 148 },
				{ 24, 109, 153 }, { 26, 115, 158 }, { 27, 122, 163 }, { 28, 128, 168 }, { 30, 134, 173 },
				{ 31, 140, 178 }, { 32, 147, 183 }, { 33, 153, 188 }, { 35, 159, 193 }, { 36, 165, 198 },
				{ 37, 172, 203 }, { 39, 178, 208 }, { 40, 184, 213 } };

		palette = new Color[rgb.length];

		System.out.println(rgb.length + " colors");

		int index = 0;
		for (int i = rgb.length - 1; i >= 0; i--) {
			palette[index] = Color.rgb(rgb[i][0], rgb[i][1], rgb[i][2]);
			index++;
		}
	}
}
