package view.primaryStage.menus;

import application.BenzenoidApplication;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.RenameCollectionPane;

public class CollectionsMenu {

	public CollectionsMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {
		BenzenoidCollectionsManagerPane collectionsPane = (BenzenoidCollectionsManagerPane) app.getPanes().getCollectionsPane();

		final Menu collectionsMenu = new Menu("_Collections");

		
		collectionsMenu.setOnShowing(e -> {
			app.switchMode(app.getPanes().getCollectionsPane());
		});

		MenuItem itemRename = new MenuItem("Rename collection");
		MenuItem itemDelete = new MenuItem("Delete benzenoid(s)");
		MenuItem itemCopy = new MenuItem("Copy benzenoid(s)");
		MenuItem itemPaste = new MenuItem("Paste benzenoid(s)");
		MenuItem itemSelect = new MenuItem("Select all");
		MenuItem unselectAllItem = new MenuItem("Unselect all");
		MenuItem operationsMenu = new MenuItem("Operations on collections");
    MenuItem checkDatabaseItem = new MenuItem("Check database");

		operationsMenu.setOnAction(e -> {
			app.switchMode(app.getPanes().getOperationPane());
		});

		itemPaste.setOnAction(e -> {
			collectionsPane.paste();
		});

		itemSelect.setOnAction(e -> {
			collectionsPane.selectAll();
		});

		unselectAllItem.setOnAction(e -> {
			collectionsPane.unselectAll();
		});

		itemRename.setOnAction(e -> {
			RenameCollectionPane root;
			root = new RenameCollectionPane(collectionsPane);
			Stage stage = new Stage();
			stage.setTitle("Rename collection");

			stage.setResizable(false);

			stage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		});

		itemDelete.setOnAction(e -> {
			BenzenoidCollectionPane currentPane = collectionsPane.getSelectedTab();
			currentPane.removeBenzenoidPanes(currentPane.getSelectedBenzenoidPanes());
			collectionsPane.log("Deleting "
					+ currentPane.getSelectedBenzenoidPanes().size() + " benzenoid(s) from " + currentPane.getName(),
					true);
		});

		itemCopy.setOnAction(e -> {
			BenzenoidCollectionPane originBenzenoidCollectionPane = collectionsPane.getSelectedTab();
			originBenzenoidCollectionPane.copy();
		});
    
    checkDatabaseItem.setOnAction(e -> {
    	collectionsPane.checkDatabase();
		});

		collectionsMenu.getItems().addAll(itemRename, itemDelete, itemCopy, itemPaste, collectionsPane.initializeMoveMenuItem(), itemSelect, unselectAllItem, operationsMenu,checkDatabaseItem);

		return collectionsMenu;
	}

}
