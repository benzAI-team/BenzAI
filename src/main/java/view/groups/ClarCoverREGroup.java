package view.groups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import benzenoid.Benzenoid;
import utils.Couple;
import utils.HexagonAromaticity;

public class ClarCoverREGroup extends MoleculeGroup {

	private final int[] clarValues;

	private Color[] paletteLocalScale;

	public ClarCoverREGroup(Benzenoid molecule, int[] clarValues) throws IOException {
		super(molecule);
		removeTexts();

		this.clarValues = clarValues;
		buildPalette();
		coloringHexagons();
		drawText();
	}

	private void drawText() {
		for (int i = 0; i < clarValues.length; i++) {
			Hexagon2 hexagon = hexagons[i];
			Couple<Double, Double> center = hexagon.getCenter();

			Text text = createText(Integer.toString(clarValues[i]));
			text.setX(center.getX() - 15.0);
			text.setY(center.getY() + 5.0);
			this.getChildren().add(text);
		}
	}

	private void buildPalette() {

		int[][] rgb = new int[][] { { 2, 3, 68 }, { 3, 9, 73 }, { 5, 15, 78 }, { 6, 22, 83 }, { 7, 28, 88 },
				{ 9, 34, 93 }, { 10, 40, 98 }, { 11, 47, 103 }, { 12, 53, 108 }, { 14, 59, 113 }, { 15, 65, 118 },
				{ 16, 72, 123 }, { 18, 78, 128 }, { 19, 84, 133 }, { 20, 90, 138 }, { 22, 97, 143 }, { 23, 103, 148 },
				{ 24, 109, 153 }, { 26, 115, 158 }, { 27, 122, 163 }, { 28, 128, 168 }, { 30, 134, 173 },
				{ 31, 140, 178 }, { 32, 147, 183 }, { 33, 153, 188 }, { 35, 159, 193 }, { 36, 165, 198 },
				{ 37, 172, 203 }, { 39, 178, 208 }, { 40, 184, 213 } };

		paletteLocalScale = new Color[rgb.length];
		Color[] paletteGlobalScale = new Color[10];

		System.out.println(rgb.length + " colors");

		int index = 0;
		for (int i = rgb.length - 1; i >= 0; i--) {
			paletteLocalScale[index] = Color.rgb(rgb[i][0], rgb[i][1], rgb[i][2]);
			index++;
		}

		int rgbIndex = 0;
		for (int i = 0; i < 10; i++) {
			paletteGlobalScale[i] = Color.rgb(rgb[rgbIndex][0], rgb[rgbIndex][1], rgb[rgbIndex][2]);
			rgbIndex += 3;
		}

	}

	private void coloringHexagons() {

		// double[] localAromaticity = aromaticity.getLocalAromaticity();
		ArrayList<HexagonAromaticity> aromaticities = new ArrayList<>();

		for (int i = 0; i < clarValues.length; i++)
			aromaticities.add(new HexagonAromaticity(i, (double) clarValues[i]));

		Collections.sort(aromaticities);

		int nbColors = 0;
		double curentValue = -1.0;

		ArrayList<Double> values = new ArrayList<>();

		for (HexagonAromaticity aromaticity : aromaticities) {
			if (aromaticity.getAromaticity() != curentValue) {
				nbColors++;
				curentValue = aromaticity.getAromaticity();
				values.add(curentValue);
			}
		}

		int scale = Math.floorDiv(paletteLocalScale.length, nbColors);

		int colorIndex = 0;
		for (int i = 0; i < nbColors; i++) {

			Color color = paletteLocalScale[colorIndex];
			double value = values.get(i);

			for (HexagonAromaticity aromaticity : aromaticities) {
				if (aromaticity.getAromaticity() == value) {
					if (value > 0.0)
						hexagons[aromaticity.getIndex()].setFill(color);
					else
						hexagons[aromaticity.getIndex()].setFill(Color.WHITE);
				}
			}

			colorIndex += scale;
		}
	}

}
