package view.ir_spectra;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
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

public class IRSpectraPane extends GridPane {

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

		this.setPadding(new Insets(20));
		this.setHgap(25);
		this.setVgap(15);

		this.setMaxSize(823, 515);
		this.setMinSize(823, 515);

		this.add(selectedPlotPane, 0, 0);

		buildList();

		GridPane pane = new GridPane();
		pane.add(listView, 0, 0);

		this.add(pane, 1, 0, 1, 2);

		Button beginButton = new Button("|<");
		Button prevButton = new Button("<");
		Button nextButton = new Button(">");
		Button endButton = new Button(">|");
		Button exportButton = new Button("Export");
		Button exportAllButton = new Button("Export all");
		Button saveDataButton = new Button("Save data");

		Button exportAmesButton = new Button("Export Ames Formats");

		exportAmesButton.setOnAction(e -> {
			//exportAmesFormats();

			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			File file = directoryChooser.showDialog(parent.getApplication().getStage());

			if (file != null) {
				try {
					exportAmesFormats(file);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});

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

		saveDataButton.setOnAction(e -> {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			File file = directoryChooser.showDialog(parent.getApplication().getStage());

			if (file != null) {

				String directoryPath = file.getAbsolutePath();

				File parametersFile = new File(directoryPath + File.separator + "parameters.txt");
				File spectraFile = new File(directoryPath + File.separator + "spectras.csv");
				File moleculeFile = new File(directoryPath + File.separator + "molecules_informations.csv");
				File intensitiesFile = new File(directoryPath + File.separator + "intensities.csv");

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

		exportButton.setOnAction(e -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Export");
			fileChooser.setInitialFileName(selectedPlotPane.getResult().getClassName() + ".png");
			File file = fileChooser.showSaveDialog(parent.getApplication().getStage());

			if (file != null)
				selectedPlotPane.exportAsPDF(file);
		});

		exportAllButton.setOnAction(e -> {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			File file = directoryChooser.showDialog(parent.getApplication().getStage());

			if (file != null) {

				String directoryPath = file.getAbsolutePath();

				for (int i = 0; i < panes.size(); i++) {

					index = (index + 1) % panes.size();
					updatePane();

          System.out.println(directoryPath + File.separator + selectedPlotPane.getResult().getClassName() + ".png");

					File plotFile = new File(directoryPath + File.separator + selectedPlotPane.getResult().getClassName() + ".png");

					selectedPlotPane.exportAsPDF(plotFile);

				}
			}

		});

		HBox buttonsHBox = new HBox(5);
		buttonsHBox.getChildren().addAll(beginButton, prevButton, nextButton, endButton, exportButton, exportAmesButton, exportAllButton,
				saveDataButton);
		buttonsHBox.setAlignment(Pos.CENTER);

		this.add(buttonsHBox, 0, 1);
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


	private void exportAmesFormats(File directory) throws IOException, Exception{

		for (ComputedPlotPane pane : panes) {
			ArrayList<String> amesFormats = pane.getAmesFormat();
			IRSpectra result = pane.getResult();

			File file = new File(directory.getAbsolutePath() + File.separator + result.getClassName() + ".xml");

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write("<pahdatabase database=\"theoretical\" version=\"3.00\" date=\"2017-08-18\" full=\"false\">\n");

			writer.write("<comment>This file was generated with BenzAI software with data from BenzDB database, version 1.1. See: https://benzenoids.lis-lab.fr/.</comment>\n");

			writer.write("  <species>\n");
			for (String amesFormat : amesFormats) {
        int index1 = amesFormat.indexOf("<specie ");
        int index2 = amesFormat.indexOf("</specie>");
        
				writer.write("    "+amesFormat.substring(index1,index2+9)+ "\n");
			}
			writer.write("</species>\n");
			writer.write("</pahdatabase>\n");
			writer.close();
		}
	}

	private void updatePane() {
		this.getChildren().remove(selectedPlotPane);
		selectedPlotPane = panes.get(index);
		this.add(selectedPlotPane, 0, 0);
		listView.getSelectionModel().select(index);
	}
}
