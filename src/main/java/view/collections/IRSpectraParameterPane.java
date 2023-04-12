package view.collections;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import spectrums.Parameter;

public class IRSpectraParameterPane extends ScrollPane {

	private VBox mainBox = new VBox();
	private BenzenoidCollectionsManagerPane catalogPane;

	public IRSpectraParameterPane(BenzenoidCollectionsManagerPane catalogPane) {
		super();
		this.catalogPane = catalogPane;
		initWindowContents();
	}

	public void initWindowContents() {

		setContent(mainBox);

		GridPane pane = new GridPane();

		pane.setPadding(new Insets(20));
		pane.setHgap(25);
		pane.setVgap(15);

		Label VMinLabel = new Label("VMin : ");
		TextField VMinField = new TextField();
		VMinField.setText("600");

		Label VMaxLabel = new Label("VMax : ");
		TextField VMaxField = new TextField();
		VMaxField.setText("1700");

		Label stepLabel = new Label("step : ");
		TextField stepField = new TextField();
		stepField.setText("1");

		Label FWHMLabel = new Label("FWHM : ");
		TextField FWHMField = new TextField();
		FWHMField.setText("30");

		Label ZTLabel = new Label("ZT : ");
		TextField ZTField = new TextField();
		ZTField.setText("1.0");

		Label giLabel = new Label("gi : ");
		TextField giField = new TextField();
		giField.setText("1.0");

		Label NLabel = new Label("N : ");
		TextField NField = new TextField();
		NField.setText("100.0");

		Label TLabel = new Label("T : ");
		TextField TField = new TextField();
		TField.setText("100.0");

		Label KBLabel1 = new Label("KB : ");
		TextField KBField1 = new TextField();
		KBField1.setText("1.380649");
		Label KBLabel2 = new Label("x 10^");
		TextField KBField2 = new TextField();
		KBField2.setText("-23");

		KBField1.setEditable(false);
		KBField2.setEditable(false);

		HBox KBBox = new HBox(5);
		KBBox.getChildren().addAll(KBLabel1, KBField1, KBLabel2, KBField2);

		Button importButton = new Button("Import parameters");
		Button applyButton = new Button("Apply");

		importButton.setOnAction(e -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			File parameterFile = fileChooser.showOpenDialog(catalogPane.getApplication().getStage());

			if (parameterFile != null) {

				BufferedReader reader;
				try {
					reader = new BufferedReader(new FileReader(parameterFile));

					ArrayList<String> lines = new ArrayList<String>();
					String line = null;

					while ((line = reader.readLine()) != null)
						lines.add(line);

					reader.close();

					Parameter parameter = new Parameter();

					parameter.setVMin(Integer.parseInt(lines.get(0).split(Pattern.quote(" = "))[1]));
					parameter.setVMax(Integer.parseInt(lines.get(1).split(Pattern.quote(" = "))[1]));
					parameter.setStep(Integer.parseInt(lines.get(2).split(Pattern.quote(" = "))[1]));
					parameter.setFWHM(Integer.parseInt(lines.get(3).split(Pattern.quote(" = "))[1]));

					parameter.setZT(Double.parseDouble(lines.get(4).split(Pattern.quote(" = "))[1]));
					parameter.setGi(Double.parseDouble(lines.get(5).split(Pattern.quote(" = "))[1]));
					parameter.setN(Double.parseDouble(lines.get(6).split(Pattern.quote(" = "))[1]));
					parameter.setT(Double.parseDouble(lines.get(7).split(Pattern.quote(" = "))[1]));

					String splittedKb = lines.get(8).split(Pattern.quote(" = "))[1];
					double factor = Double.parseDouble(splittedKb.split(" ")[0]);
					int exponent = Integer.parseInt(splittedKb.split(" ")[1]);

					parameter.setKb(factor * ((double) Math.pow(10, exponent)));

					VMinField.setText(Integer.toString(parameter.getVMin()));
					VMaxField.setText(Integer.toString(parameter.getVMax()));
					stepField.setText(Integer.toString(parameter.getStep()));
					FWHMField.setText(Integer.toString(parameter.getFWHM()));
					ZTField.setText(Double.toString(parameter.getZT()));
					giField.setText(Double.toString(parameter.getGi()));
					NField.setText(Double.toString(parameter.getN()));
					TField.setText(Double.toString(parameter.getT()));
					KBField1.setText(Double.toString(factor));
					KBField2.setText(Integer.toString(exponent));

				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});

		applyButton.setOnAction(e -> {

			boolean ok = true;
			Parameter parameter = new Parameter();

			try {

				parameter.setVMin(Integer.parseInt(VMinField.getText()));
				parameter.setVMax(Integer.parseInt(VMaxField.getText()));
				parameter.setStep(Integer.parseInt(stepField.getText()));
				parameter.setFWHM(Integer.parseInt(FWHMField.getText()));
				parameter.setZT(Double.parseDouble(ZTField.getText()));
				parameter.setGi(Double.parseDouble(giField.getText()));
				parameter.setN(Double.parseDouble(NField.getText()));
				parameter.setT(Double.parseDouble(TField.getText()));

				double factor = Double.parseDouble(KBField1.getText());
				int exponent = Integer.parseInt(KBField2.getText());

				parameter.setKb(factor * ((double) Math.pow(10, exponent)));

				Stage stage = (Stage) this.getScene().getWindow();
				stage.close();

			} catch (NumberFormatException exception) {
				System.out.println("Invalid fields");
				ok = false;
			}

			if (ok) {
				catalogPane.setIRSpectraParameter(parameter);
				System.out.println(parameter.toString());
			}
		});

		pane.add(VMinLabel, 0, 0);
		pane.add(VMinField, 1, 0);

		pane.add(VMaxLabel, 0, 1);
		pane.add(VMaxField, 1, 1);

		pane.add(stepLabel, 0, 2);
		pane.add(stepField, 1, 2);

		pane.add(FWHMLabel, 0, 3);
		pane.add(FWHMField, 1, 3);

		pane.add(ZTLabel, 0, 4);
		pane.add(ZTField, 1, 4);

		pane.add(giLabel, 0, 5);
		pane.add(giField, 1, 5);

		pane.add(NLabel, 0, 6);
		pane.add(NField, 1, 6);

		pane.add(TLabel, 0, 7);
		pane.add(TField, 1, 7);

		//pane.add(KBLabel1, 0, 8);
		//pane.add(KBBox, 1, 8);

		HBox buttonsHBox = new HBox(5);
		buttonsHBox.getChildren().addAll(importButton, applyButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		pane.add(buttonsHBox, 0, 9, 2, 1);

		mainBox.getChildren().addAll(pane);
	}

}
