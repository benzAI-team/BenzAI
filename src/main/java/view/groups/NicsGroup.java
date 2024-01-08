package view.groups;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import benzenoid.Benzenoid;
import solveur.Aromaticity;
import solveur.Aromaticity.RIType;
import utils.HexagonAromaticity;

import java.io.IOException;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;

public class NicsGroup extends MoleculeGroup {

	private final String NICSValues;
	
	
	public NicsGroup(Benzenoid molecule) throws IOException {
		super(molecule);
    this.NICSValues = molecule.getDatabaseInformation().findNICS().get();
		removeTexts();
		writeNICS();
	}

	private void writeNICS() {
		texts = new ArrayList<>();
    Locale locale = new Locale( "en", "US" );
    
		for (int i = 0; i < hexagons.length; i++) {
			
			Hexagon2 hexagon = hexagons[i];
			StringBuilder builder = new StringBuilder();
			Tooltip tooltip;
			builder.append("H").append(i);
      
      String[] values = NICSValues.split(" ");

			if (writeText) {  
        
        String valueStr = String.format(locale,"%1.1f",Double.parseDouble(values[i]));
      
				tooltip = new Tooltip(builder.toString());

				Text text;
				
				if ("0.0".equals(valueStr)) {
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
          if(valueStr.length() == 5) {
            text.setX(centersCoords[i].getX() + xShift - 17.0);
          }
          else {
            text.setX(centersCoords[i].getX() + xShift - 15.0);
          }
					text.setY(centersCoords[i].getY() + yShift + 5.0);
					text.setFill(Color.BLACK);
					text.setStroke(Color.BLACK);
				}
				Tooltip.install(text, tooltip);
				
				texts.add(text);

				this.getChildren().add(text);
			}
			
			tooltip = new Tooltip(builder.toString());
			Tooltip.install(hexagon, tooltip);
		}
	}
}
