package view.primaryStage.menus;

import application.BenzenoidApplication;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import view.help.HelpPane;

public class HelpMenu {

	public HelpMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {

			Menu helpMenu = new Menu("_Help");

			MenuItem helpItem = new MenuItem("Help content");
			MenuItem aboutItem = new MenuItem("About BenzAI");

			helpItem.setOnAction(e -> {
				Region helpPane = new HelpPane();
				Stage stage = new Stage();
				stage.setTitle("Help");

				Scene scene = new Scene(helpPane);
				scene.getStylesheets().add("/resources/style/application.css");

				stage.setScene(scene);
				stage.show();
			});

			aboutItem.setOnAction(e -> app.displayAboutWindow());

			helpMenu.getItems().addAll(helpItem, aboutItem);

			return helpMenu;

	}
	
}
