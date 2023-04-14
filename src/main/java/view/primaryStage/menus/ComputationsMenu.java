package view.primaryStage.menus;

import application.BenzenoidApplication;
import collection_operations.CollectionOperation;
import collection_operations.CollectionOperationSet;
import javafx.scene.control.Menu;
import view.collections.BenzenoidCollectionsManagerPane;

public class ComputationsMenu {

	public ComputationsMenu() {
		// TODO Auto-generated constructor stub
	}

	public static Menu build(BenzenoidApplication app) {
		BenzenoidCollectionsManagerPane collectionsPane = (BenzenoidCollectionsManagerPane) app.getPanes()
				.getCollectionsPane();
		Menu computationsMenu = new Menu("C_omputations");

		computationsMenu.setOnShowing(e -> app.switchMode(collectionsPane));

		for(CollectionOperation computation : CollectionOperationSet.getCollectionComputationSet()) {
			computationsMenu.getItems().add(computation.getMenuItem());
			computation.getMenuItem().setOnAction(e -> computation.execute(collectionsPane));
		}
		return computationsMenu;
	}
}
