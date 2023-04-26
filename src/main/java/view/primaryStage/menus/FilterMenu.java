package view.primaryStage.menus;

import application.BenzenoidApplication;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class FilterMenu {

	public FilterMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {

		Menu filterMenu = new Menu("Fi_lter");

		final MenuItem menuItem = new MenuItem("Filter");
		filterMenu.getItems().add(menuItem);

		filterMenu.setOnAction(e -> app.switchMode(app.getPanes().getFilteringPane()));

		return filterMenu;

	}
}
