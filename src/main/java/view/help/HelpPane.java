package view.help;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class HelpPane extends GridPane {

	private ArrayList<HelpHBox> helpBoxes;

	private GridPane gridPane;

	private TextArea curentArea;

	public HelpPane() {
		super();
		initialize();
	}

	private void initialize() {
		gridPane = new GridPane();
		ScrollPane leftScrollPane = new ScrollPane();

		leftScrollPane.setContent(gridPane);

		curentArea = new TextArea();
		curentArea.setEditable(false);

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(25);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(75);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);

		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(100);

		this.getColumnConstraints().addAll(col1, col2);
		this.getRowConstraints().add(row1);

		this.add(leftScrollPane, 0, 0, 1, 2);
		this.add(curentArea, 1, 0, 1, 2);

		GridPane.setFillWidth(leftScrollPane, true);
		GridPane.setFillHeight(leftScrollPane, true);

		helpBoxes = HelpHBox.buildBoxes(this);
		refreshBoxes();
	}

	public void refreshBoxes() {

		gridPane.getChildren().clear();
		int index = 0;

		for (HelpHBox helpBox : helpBoxes) {
			gridPane.add(helpBox, 0, index);
			index++;

			if (helpBox.isOpen()) {
				for (SubHelpHBox subHelpBox : helpBox.getSubBoxes()) {
					gridPane.add(subHelpBox, 0, index);
					index++;
				}
			}
		}
	}

	public void refreshArea(String text) {
		// rightScrollPane.setContent(area);
		curentArea.setText(text);
	}
}
