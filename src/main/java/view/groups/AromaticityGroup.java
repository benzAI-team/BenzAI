package view.groups;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import molecules.Molecule;
import solveur.Aromaticity;
import solveur.Aromaticity.RIType;
import utils.HexagonAromaticity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

public class AromaticityGroup extends MoleculeGroup {

	public static AromaticityDisplayType aromaticityDisplayType;

	private Color[] paletteLocalScale;
	private Color[] paletteGlobalScale;
	
	private final Aromaticity aromaticity;
	
	
	public AromaticityGroup(Molecule molecule, Aromaticity aromaticity)
			throws IOException {

		super(molecule);
		this.aromaticity = aromaticity;
		removeTexts();
		writeAromaticity();
		buildPalette();
		refreshColors();
		//coloringHexagonsWithLocalScale();
	}

	private void writeAromaticity() {
		texts = new ArrayList<>();

		for (int i = 0; i < hexagons.length; i++) {
			
			Hexagon2 hexagon = hexagons[i];
			StringBuilder builder = new StringBuilder();
			Tooltip tooltip;
			builder.append("H").append(i);

			if (writeText) {

				BigDecimal bd = BigDecimal.valueOf(aromaticity.getLocalAromaticity()[i]).setScale(2, RoundingMode.HALF_UP);

				builder.append(": ").append(bd.doubleValue());
				
				tooltip = new Tooltip(builder.toString());
				
				String valueStr = Double.toString(bd.doubleValue());
				
				Text text;
				
				if (valueStr.equals("0.0")) {
					valueStr = "0.00";
					
					text = createText(valueStr);
					text.setX(centersCoords[i].getX() + xShift - 15.0);
					text.setY(centersCoords[i].getY() + yShift + 5.0);
					text.setFill(Color.BLACK);
					text.setStroke(Color.BLACK);
				}
				
				else {
					
					if(valueStr.length() < 4) {
						valueStr = valueStr + "0".repeat(4 - valueStr.length());
					}
					
					text = createText(valueStr);
					text.setX(centersCoords[i].getX() + xShift - 15.0);
					text.setY(centersCoords[i].getY() + yShift + 5.0);
					text.setFill(Color.WHITE);
					text.setStroke(Color.WHITE);
				}
				Tooltip.install(text, tooltip);
				
				texts.add(text);

				this.getChildren().add(text);
			}
			
			tooltip = new Tooltip(builder.toString());
			Tooltip.install(hexagon, tooltip);
		}
	}

	private void coloringHexagonsWithLocalScale() {

		double[] localAromaticity = aromaticity.getLocalAromaticity();
		ArrayList<HexagonAromaticity> aromaticities = new ArrayList<>();

		for (int i = 0; i < localAromaticity.length; i++)
			aromaticities.add(new HexagonAromaticity(i, localAromaticity[i]));

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

		for (int i = 0; i < hexagons.length; i++) {

			final int index = i;
			final double [] localCircuits = aromaticity.getLocalCircuits()[i];

			Hexagon2 hexagon = hexagons[i];
			hexagon.setOnMouseClicked(e -> {

				StringBuilder informations = new StringBuilder();
				informations.append("H").append(index).append(" : ");

				for (double localCircuit : localCircuits) {
					informations.append(localCircuit).append(" ");
				}

				informations.append("(").append(aromaticity.getLocalAromaticity()[index]).append(")");

			});
		}
	}

	private void coloringHexagonsWithGlobalScale() {
		
		double [] localAromaticity = aromaticity.getLocalAromaticity();
		
		for (int i = 0 ; i < localAromaticity.length ; i++) {
			
			double value = localAromaticity[i];
			Color color = Color.WHITE;
			
			if (value > 0 && value <= 0.1)
				color = paletteGlobalScale[9];
			
			else if (value > 0.1 && value <= 0.2)
				color = paletteGlobalScale[8];
			
			else if (value > 0.2 && value <= 0.3)
				color = paletteGlobalScale[7];
			
			else if (value > 0.3 && value <= 0.4)
				color = paletteGlobalScale[6];
			
			else if (value > 0.4 && value <= 0.5)
				color = paletteGlobalScale[5];
			
			else if (value > 0.5 && value <= 0.6)
				color = paletteGlobalScale[4];
			
			else if (value > 0.6 && value <= 0.7)
				color = paletteGlobalScale[3]; 
			
			else if (value > 0.7 && value <= 0.8)
				color = paletteGlobalScale[2];
			
			else if (value > 0.8 && value <= 0.9)
				color = paletteGlobalScale[1];
			
			else if (value > 0.9 && value <= 1.0) 
				color = paletteGlobalScale[0];
			
			hexagons[i].setFill(color);
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
		paletteGlobalScale = new Color[10];
		
		System.out.println(rgb.length + " colors");
		
		
		
		int index = 0;
		for (int i = rgb.length - 1; i >= 0; i--) {
			paletteLocalScale[index] = Color.rgb(rgb[i][0], rgb[i][1], rgb[i][2]);
			index++;
		}
		
		int rgbIndex = 0;
		for (int i = 0 ; i < 10 ; i++) {
			paletteGlobalScale[i] = Color.rgb(rgb[rgbIndex][0], rgb[rgbIndex][1], rgb[rgbIndex][2]);
			rgbIndex += 3;
		}

	}
	
	public void refreshColors() {
		
		if (aromaticityDisplayType == AromaticityDisplayType.LOCAL_COLOR_SCALE)
			coloringHexagonsWithLocalScale();
		
		else
			coloringHexagonsWithGlobalScale();
			
	}
	
	public void refreshRIType(RIType type) {
		aromaticity.setType(type);
		refreshColors();
		
		for (Text text : texts) {
			this.getChildren().remove(text);
		}
		
		writeAromaticity();
	}

}
