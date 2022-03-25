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

		File fileGenerator = getFile("generator.txt");
		SubHelpHBox generatorBox = new SubHelpHBox("Generating benzenoids", fileGenerator, helpPane);
		inputBox.addSubBox(generatorBox);

		File fileDraw = getFile("draw.txt");
		SubHelpHBox drawBox = new SubHelpHBox("Drawing benzenoids", fileDraw, helpPane);
		inputBox.addSubBox(drawBox);

		File fileDatabase = getFile("database.txt");
		SubHelpHBox databaseBox = new SubHelpHBox("Fetching benzenoids from database", fileDatabase, helpPane);
		inputBox.addSubBox(databaseBox);

		File fileImport = getFile("import.txt");
		SubHelpHBox importBox = new SubHelpHBox("Import benzenoid file", fileImport, helpPane);
		inputBox.addSubBox(importBox);

		boxes.add(inputBox);

		return boxes;
	}
}