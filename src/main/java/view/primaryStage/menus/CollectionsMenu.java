package view.primaryStage.menus;

import application.BenzenoidApplication;
import collection_operations.CollectionOperation;
import collection_operations.CollectionOperationSet;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import view.collections.BenzenoidCollectionsManagerPane;

public class CollectionsMenu {

	public CollectionsMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {
		BenzenoidCollectionsManagerPane collectionsPane = (BenzenoidCollectionsManagerPane) app.getPanes().getCollectionsPane();

		final Menu collectionsMenu = new Menu("_Collections");

		
		collectionsMenu.setOnShowing(e -> app.switchMode(app.getPanes().getCollectionsPane()));

		MenuItem operationsMenu = new MenuItem("Operations on collections");
		operationsMenu.setOnAction(e -> app.switchMode(app.getPanes().getOperationPane()));
		collectionsMenu.getItems().addAll(operationsMenu, collectionsPane.initializeMoveMenuItem());

		for(CollectionOperation operation : CollectionOperationSet.getCollectionSimpleOperationSet()){
			collectionsMenu.getItems().add(operation.getMenuItem());
			operation.getMenuItem().setOnAction(e -> operation.execute(collectionsPane));
		}

		return collectionsMenu;
	}

}
