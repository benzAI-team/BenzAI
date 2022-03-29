package view.help;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class HelpHBox extends HBox {

	private HelpPane parent;

	private String name;
	private ArrayList<SubHelpHBox> subBoxes;

	private OpenButton openButton;

	private boolean open;

	public HelpHBox(String name, HelpPane parent) {

		super(1.0);
		this.name = name;
		this.parent = parent;
		initialize();
	}

	private void initialize() {
		open = false;
		subBoxes = new ArrayList<>();
		openButton = new OpenButton(this);

		Label label = new Label(name);

		this.getChildren().addAll(openButton, label);
	}

	public String getName() {
		return name;
	}

	public void addSubBox(SubHelpHBox subBox) {
		subBoxes.add(subBox);
	}

	public ArrayList<SubHelpHBox> getSubBoxes() {
		return subBoxes;
	}

	public void open() {
		open = true;

		parent.refreshBoxes();
	}

	public void close() {
		open = false;
		parent.refreshArea("");
		parent.refreshBoxes();
	}

	public boolean isOpen() {
		return open;
	}

	private static File getFile(String filename) {
		File file = new File("/resources/doc/" + filename);
		return file;
	}

	public static ArrayList<HelpHBox> buildBoxes(HelpPane helpPane) {

		ArrayList<HelpHBox> boxes = new ArrayList<>();

		/*
		 * Input
		 */

		HelpHBox inputBox = new HelpHBox("How to fill a collection", helpPane);

		SubHelpHBox generatorBox = new SubHelpHBox("Generating benzenoids", "generator.txt", helpPane);
		inputBox.addSubBox(generatorBox);

		SubHelpHBox drawBox = new SubHelpHBox("Drawing benzenoids", "draw.txt", helpPane);
		inputBox.addSubBox(drawBox);

		SubHelpHBox databaseBox = new SubHelpHBox("Fetching benzenoids from database", "database.txt", helpPane);
		inputBox.addSubBox(databaseBox);

		SubHelpHBox importBox = new SubHelpHBox("Import benzenoid file", "import.txt", helpPane);
		inputBox.addSubBox(importBox);

		boxes.add(inputBox);

		return boxes;
	}
}
