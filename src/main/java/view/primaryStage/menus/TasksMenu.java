package view.primaryStage.menus;

import application.BenzenoidApplication;
import javafx.scene.control.Menu;

public class TasksMenu {

	public TasksMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {
		app.setTasksMenu(new Menu("_Active tasks"));
		app.addTask("None");
		return app.getTasksMenu();

	}
}
