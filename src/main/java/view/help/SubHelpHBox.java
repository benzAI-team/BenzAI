package view.help;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SubHelpHBox extends HBox {

	private final HelpPane parent;

	private final String name;
	private final String filename;
	private String text;

	public SubHelpHBox(String name, String filename, HelpPane parent) {
		super(1.0);
		this.name = name;
		this.filename = filename;
		this.parent = parent;

		initialize();
	}

	private void initialize() {
		buildArea();

		ImageView emptyIcon = new ImageView(new Image("/resources/graphics/empty_16_16.png"));

		Label label = new Label(name);
		this.getChildren().addAll(emptyIcon, label);

		this.setOnMouseClicked(e -> parent.refreshArea(text));

	}

	private void buildArea() {

		StringBuilder builder = new StringBuilder();

		try {
      System.out.println("File "+filename);
      InputStream in = getClass().getResourceAsStream("/resources/doc/"+filename);
			assert in != null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		text = builder.toString();
	}

}
