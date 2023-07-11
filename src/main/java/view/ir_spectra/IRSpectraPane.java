package view.ir_spectra;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import spectrums.IRSpectra;
import spectrums.Parameter;
import spectrums.ResultLogFile;
import utils.Couple;
import view.collections.BenzenoidCollectionsManagerPane;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class IRSpectraPane extends BorderPane {

	private GridPane gridPane;

	private final BenzenoidCollectionsManagerPane parent;

	private final Parameter parameter;
	private final ArrayList<ComputedPlotPane> panes;
	private ComputedPlotPane selectedPlotPane;
	private int index;

	ListView<String> listView = new ListView<>();

	public IRSpectraPane(ArrayList<ComputedPlotPane> panes, BenzenoidCollectionsManagerPane parent,
			Parameter parameter) {
		this.panes = panes;
		this.parent = parent;
		this.parameter = parameter;
		index = 0;
		selectedPlotPane = panes.get(index);
		initialize();
	}

	public Parameter getParameter() {
		return parameter;
	}

	private void initialize() {

		gridPane = new GridPane();

		gridPane.setPadding(new Insets(20));
		gridPane.setHgap(25);
		gridPane.setVgap(15);

		gridPane.add(selectedPlotPane, 0, 0);

		buildList();

		GridPane pane = new GridPane();
		pane.add(listView, 0, 0);

		gridPane.add(pane, 1, 0, 1, 2);

		Button beginButton = new Button("|<");
		Button prevButton = new Button("<");
		Button nextButton = new Button(">");
		Button endButton = new Button(">|");
		/*
		Button exportButton = new Button("Export");
		Button exportAllButton = new Button("Export all");
		Button saveDataButton = new Button("Save data");
		Button exportAmesButton = new Button("Export Ames Formats");
		*/


		beginButton.setOnAction(e -> {
			index = 0;
			updatePane();
		});

		prevButton.setOnAction(e -> {
			index = index - 1;

			if (index < 0)
				index = panes.size() - 1;

			updatePane();
		});

		nextButton.setOnAction(e -> {
			index = (index + 1) % panes.size();
			updatePane();
		});

		endButton.setOnAction(e -> {
			index = panes.size() - 1;
			updatePane();
		});



		HBox buttonsHBox = new HBox(5);
		buttonsHBox.getChildren().addAll(beginButton, prevButton, nextButton, endButton);
		buttonsHBox.setAlignment(Pos.CENTER);

		gridPane.add(buttonsHBox, 0, 1);

		//this.autosize();

		//this.setMaxSize(973, 615);
		//this.setMinSize(973, 615);

		this.setTop(buildMenu());
		this.setBottom(gridPane);
		//this.resize(973,615);
	}

	private void buildList() {

		listView.setMinSize(249, 447);
		listView.setMaxSize(249, 447);

		ArrayList<String> listString = new ArrayList<>();
		for (ComputedPlotPane pane : panes)
			listString.add(pane.getResult().getClassName());

		ObservableList<String> items = FXCollections.observableArrayList(listString);

		listView.setItems(items);

		listView.setOnMouseClicked(event -> {
			String selection = listView.getSelectionModel().getSelectedItem();

			for (int i = 0; i < panes.size(); i++) {

				ComputedPlotPane pane = panes.get(i);
				if (pane.getResult().getClassName().equals(selection)) {
					index = i;
					updatePane();
					break;
				}
			}
		});

		listView.setOnKeyPressed(e -> {

			if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.LEFT) {
				index = index - 1;

				if (index < 0)
					index = panes.size() - 1;

				updatePane();
			}

			if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.RIGHT) {
				index = (index + 1) % panes.size();
				updatePane();
			}

		});
	}

	private void exportParameters(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(parameter.toString());
		writer.close();
	}

	private void exportSpectres(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		for (ComputedPlotPane pane : panes) {
			IRSpectra result = pane.getResult();
			for (Entry<String, Couple<ArrayList<Double>, ResultLogFile>> entry : result.getSpectres().entrySet()) {

				String moleculeName = entry.getKey();
				writer.write(moleculeName + "\n");

				int V = parameter.getVMin();
				for (Double spectrum : entry.getValue().getX()) {

					writer.write(V + "\t" + spectrum + "\n");
					V += parameter.getStep();
				}
				writer.write("\n");
			}
		}

		writer.close();
	}

	private void exportMoleculesInformations(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		for (ComputedPlotPane pane : panes) {

			IRSpectra resultSpectrum = pane.getResult();

			writer.write(resultSpectrum.getClassName() + "\n");

			for (Entry<String, Double> entry : resultSpectrum.getFinalEnergies().entrySet()) {

				String moleculeName = entry.getKey();
				Double finalEnergy = entry.getValue();
				Double irregularity = resultSpectrum.getIrregularities().get(moleculeName);

				writer.write(moleculeName + "\t" + finalEnergy + "\t" + irregularity + "\n");
			}
		}

		writer.close();

	}

	private void exportIntensities(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write("INTENSITIES\n");
		for (ComputedPlotPane pane : panes)
			writer.write(pane.getResult().intensitiesToString() + "\n");

		writer.close();
	}

	private void exportAmesFormats(File directory) throws IOException{

		for (ComputedPlotPane pane : panes) {
			ArrayList<String> amesFormats = pane.getAmesFormat();
			IRSpectra result = pane.getResult();

			File file = new File(directory.getAbsolutePath() + "/" + result.getClassName() + ".xml");

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write("<pahdatabase database=\"theoretical\" version=\"3.00\" date=\"2017-08-18\" full=\"false\">\n");

			writer.write("<comment>This file was generated with BenzAI software.</comment>\n");

			writer.write("<species>\n");
			for (String amesFormat : amesFormats) {
				writer.write(amesFormat + "\n");
			}
			writer.write("</species>\n");
			writer.write("</pahdatabase>\n");
			writer.close();

		}


	}

	private MenuBar buildMenu() {

		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("_File");

		MenuItem exportCurveItem = new MenuItem("Export curve");
		MenuItem exportAllCurvesItem = new MenuItem("Export all curves");
		MenuItem exportAmesFormatsItem = new MenuItem("Export to Ames Format");
		MenuItem exportDataItem = new MenuItem("Export IR spectra data");

		exportCurveItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Export");
			fileChooser.setInitialFileName(selectedPlotPane.getResult().getClassName() + ".png");
			File file = fileChooser.showSaveDialog(parent.getApplication().getStage());

			if (file != null)
				selectedPlotPane.exportAsPDF(file);
		});

		exportAllCurvesItem.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			File file = directoryChooser.showDialog(parent.getApplication().getStage());

			if (file != null) {

				String directoryPath = file.getAbsolutePath();
				boolean unix;

				unix = directoryPath.split(Pattern.quote("\\")).length == 0;

				for (int i = 0; i < panes.size(); i++) {

					index = (index + 1) % panes.size();
					updatePane();

					File plotFile;
					if (unix)
						plotFile = new File(directoryPath + "/" + selectedPlotPane.getResult().getClassName() + ".png");
					else
						plotFile = new File(
								directoryPath + "\\" + selectedPlotPane.getResult().getClassName() + ".png");

					selectedPlotPane.exportAsPDF(plotFile);

				}
			}
		});

		exportAmesFormatsItem.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			File file = directoryChooser.showDialog(parent.getApplication().getStage());

			if (file != null) {
				try {
					exportAmesFormats(file);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});

		exportDataItem.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			File file = directoryChooser.showDialog(parent.getApplication().getStage());

			if (file != null) {

				String directoryPath = file.getAbsolutePath();

				String separator;

				if (directoryPath.split(Pattern.quote("\\")).length > 1) {
					separator = "\\";
				} else {
					separator = "/";
				}

				File parametersFile = new File(directoryPath + separator + "parameters.txt");
				File spectraFile = new File(directoryPath + separator + "spectras.csv");
				File moleculeFile = new File(directoryPath + separator + "molecules_informations.csv");
				File intensitiesFile = new File(directoryPath + separator + "intensities.csv");

				try {
					exportParameters(parametersFile);
					exportSpectres(spectraFile);
					exportMoleculesInformations(moleculeFile);
					exportIntensities(intensitiesFile);

				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});

		fileMenu.getItems().addAll(exportCurveItem, exportAllCurvesItem, exportAmesFormatsItem, exportDataItem);

		menuBar.getMenus().add(fileMenu);

		return menuBar;
	}

	private void updatePane() {
		gridPane.getChildren().remove(selectedPlotPane);
		selectedPlotPane = panes.get(index);
		gridPane.add(selectedPlotPane, 0, 0);
		listView.getSelectionModel().select(index);
	}
}
