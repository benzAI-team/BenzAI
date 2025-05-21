package view.primaryStage.menus;

import application.BenzenoidApplication;
import benzenoid.sort.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import view.collections.BenzenoidCollectionsManagerPane;

public class SortMenu {

	public SortMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {
		BenzenoidCollectionsManagerPane collectionsPane =  (BenzenoidCollectionsManagerPane) app.getPanes().getCollectionsPane();

		Menu sortMenu = new Menu("_Sort");
		Menu nbCarbonsItem = new Menu("Number of carbons");
		Menu nbHydrogensItem = new Menu("Number of hydrogens");
		Menu nbHexagonsItem = new Menu("Number of hexagons");
		Menu nbKekuleStructuresItem = new Menu("Number of Kekulé structures");
		Menu clarNumberItem = new Menu("Clar Number");
		Menu irregularityItem = new Menu("Irregularity");
		Menu reItem = new Menu("Global Resonance Energy");
		
		sortMenu.setOnShowing(e -> app.switchMode(collectionsPane));

		/*
		 * Nb Carbons
		 */

		MenuItem nbCarbonsIncreasing = new MenuItem("Increasing");
		MenuItem nbCarbonsDecreasing = new MenuItem("Decreasing");

		nbCarbonsIncreasing.setOnAction(e -> collectionsPane.sort(new NbCarbonsComparator(), false));

		nbCarbonsDecreasing.setOnAction(e -> collectionsPane.sort(new NbCarbonsComparator(), true));

		nbCarbonsItem.getItems().addAll(nbCarbonsIncreasing, nbCarbonsDecreasing);

		/*
		 * Nb hydrogens
		 */

		MenuItem nbHydrogensIncreasing = new MenuItem("Increasing");
		MenuItem nbHydrogensDecreasing = new MenuItem("Decreasing");

		nbHydrogensIncreasing.setOnAction(e -> collectionsPane.sort(new NbHydrogensComparator(), false));

		nbHydrogensDecreasing.setOnAction(e -> collectionsPane.sort(new NbHydrogensComparator(), true));

		nbHydrogensItem.getItems().addAll(nbHydrogensIncreasing, nbHydrogensDecreasing);

		/*
		 * Nb Hexagons
		 */

		MenuItem nbHexagonsIncreasing = new MenuItem("Increasing");
		MenuItem nbHexagonsDecreasing = new MenuItem("Decreasing");

		nbHexagonsIncreasing.setOnAction(e -> collectionsPane.sort(new NbHexagonsComparator(), false));

		nbHexagonsDecreasing.setOnAction(e -> collectionsPane.sort(new NbHexagonsComparator(), true));

		nbHexagonsItem.getItems().addAll(nbHexagonsIncreasing, nbHexagonsDecreasing);

		/*
		 * Nb Kekule Structures
		 */

		MenuItem nbKekuleStructuresIncreasing = new MenuItem("Increasing");
		MenuItem nbKekuleStructuresDecreasing = new MenuItem("Decreasing");

		nbKekuleStructuresIncreasing.setOnAction(e -> collectionsPane.sort(new NbKekuleStructuresComparator(), false));

		nbKekuleStructuresDecreasing.setOnAction(e -> collectionsPane.sort(new NbKekuleStructuresComparator(), true));

		nbKekuleStructuresItem.getItems().addAll(nbKekuleStructuresIncreasing, nbKekuleStructuresDecreasing);


		/*
		 * Clar Number
		 */

		MenuItem clarNumberIncreasing = new MenuItem("Increasing");
		MenuItem clarNumberDecreasing = new MenuItem("Decreasing");

		clarNumberIncreasing.setOnAction(e -> collectionsPane.sort(new ClarNumberComparator(), false));

		clarNumberDecreasing.setOnAction(e -> collectionsPane.sort(new ClarNumberComparator(), true));

		clarNumberItem.getItems().addAll(clarNumberIncreasing, clarNumberDecreasing);

		/*
		 * Irregularity
		 */

		MenuItem irregularityIncreasing = new MenuItem("Increasing");
		MenuItem irregularityDecreasing = new MenuItem("Decreasing");

		irregularityIncreasing.setOnAction(e -> collectionsPane.sort(new IrregularityComparator(), false));

		irregularityDecreasing.setOnAction(e -> collectionsPane.sort(new IrregularityComparator(), true));

		irregularityItem.getItems().addAll(irregularityIncreasing, irregularityDecreasing);

		/*
		 * Resonance Energy
		 */

		MenuItem reIncreasingItem = new MenuItem("Increasing");
		MenuItem reDecreasingItem = new MenuItem("Decreasing");
		
		reIncreasingItem.setOnAction(e -> collectionsPane.sort(new ResonanceEnergyComparator(), false));
		
		reDecreasingItem.setOnAction(e -> collectionsPane.sort(new ResonanceEnergyComparator(), true));
		
		reItem.getItems().addAll(reIncreasingItem, reDecreasingItem);
		
		sortMenu.getItems().addAll(nbCarbonsItem, nbHydrogensItem, nbHexagonsItem, nbKekuleStructuresItem, clarNumberItem,
				irregularityItem/*, reItem*/);

		return sortMenu;

	}
}
