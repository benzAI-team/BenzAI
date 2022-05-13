package view.collections;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import classifier.Irregularity;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import molecules.Molecule;
import solution.BenzenoidSolution;
import solveur.Aromaticity;
import solveur.Aromaticity.RIType;
import spectrums.ResultLogFile;
import view.groups.AromaticityGroup;

public class BenzenoidPane extends BorderPane implements Comparable<BenzenoidPane> {

	private BenzenoidCollectionPane benzenoidSetPane;

	private int nbCrowns;

	@SuppressWarnings("unused")
	private String solution;

	private Group benzenoidDraw;
	private String name;

	private String description;
	private String frequencies;
	private String intensities;
	private String energies;

	private int index;

	private boolean isSelected;

	private ArrayList<Integer> verticesSolution;

	private boolean isDrawMolecule;

	private BenzenoidSolution benzenoidSolution;
	private int[] hexagonsCorrespondances;

	private HBox descriptionBox;

	public BenzenoidPane(BenzenoidCollectionPane parameterPane, int nbCrowns, String solution, Group benzenoidDraw,
			String description, ArrayList<Integer> verticesSolution, int index, boolean isDrawMolecule) {

		super();

		this.benzenoidSetPane = parameterPane;

		this.nbCrowns = nbCrowns;
		this.solution = solution;
		this.benzenoidDraw = benzenoidDraw;
		this.name = description;
		
		if (name == null)
			name = "";
		
		this.index = index;

		isSelected = false;

		this.setStyle("-fx-border-color: black;" + "-fx-border-width: 4;" + "-fx-border-radius: 10px;");

		addItems();

		this.verticesSolution = verticesSolution;
		this.isDrawMolecule = isDrawMolecule;

		this.setOnMouseEntered(e -> {
			if (!benzenoidSetPane.isLock()) {
				benzenoidSetPane.setHoveringPane(this);
				benzenoidSetPane.setDescription(buildDescription());
				benzenoidSetPane.setFrequencies(buildFrequencies());
				benzenoidSetPane.setEnergies(buildEnergies());
				benzenoidSetPane.setIntensities(buildIntensities());
			}
		});

		this.setOnMouseExited(e -> {
			if (!benzenoidSetPane.isLock()) {
				benzenoidSetPane.setHoveringPane(null);
				benzenoidSetPane.setDescription("");
				benzenoidSetPane.setFrequencies("");
				benzenoidSetPane.setEnergies("");
				benzenoidSetPane.setIntensities("");
			}
		});
	}

	public BenzenoidPane(BenzenoidCollectionPane parameterPane, int nbCrowns, String solution, Group benzenoidDraw,
			String description, ArrayList<Integer> verticesSolution, int index, boolean isDrawMolecule,
			BenzenoidSolution benzenoidSolution, int[] hexagonsCorrespondances) {

		super();

		this.benzenoidSetPane = parameterPane;

		this.nbCrowns = nbCrowns;
		this.solution = solution;
		this.benzenoidDraw = benzenoidDraw;
		this.name = description;

		if (name == null)
			name = "";
		
		this.index = index;

		isSelected = false;

		this.setStyle("-fx-border-color: black;" + "-fx-border-width: 4;" + "-fx-border-radius: 10px;");

		addItems();

		this.verticesSolution = verticesSolution;
		this.isDrawMolecule = isDrawMolecule;

		this.benzenoidSolution = benzenoidSolution;
	}

	public int getIndex() {
		return index;
	}

	private void addItems() {

		Label descriptionLabel = new Label(name);
		descriptionLabel.setMaxWidth(Double.MAX_VALUE);
		descriptionLabel.setAlignment(Pos.CENTER);

		descriptionBox = new HBox(3.0);

		descriptionBox.getChildren().add(descriptionLabel);

		BorderPane.setAlignment(descriptionBox, Pos.CENTER_LEFT);
		this.setRight(descriptionBox);
		this.setCenter(benzenoidDraw);
		BorderPane.setMargin(benzenoidDraw, new Insets(10.0));

		this.setOnMouseClicked(e -> {

			if (e.getButton() == MouseButton.PRIMARY) {
				if (!isSelected)
					select();

				else
					unselect();
			}

			else if (e.getButton() == MouseButton.SECONDARY) {
				System.out.println("Click droit");
			}

		});

		this.setMinSize(300, 200);
	}

	public void unselect() {

		benzenoidSetPane.setLock(false);

		isSelected = false;
		benzenoidSetPane.removeSelectedBenzenoidPane(this);

		benzenoidSetPane.setDescription("");
		benzenoidSetPane.setFrequencies("");
		benzenoidSetPane.setEnergies("");
		benzenoidSetPane.setIntensities("");

		// benzenoidSetPane.setCommentArea("");

		this.setStyle("-fx-border-color: black;" + "-fx-border-width: 4;" + "-fx-border-radius: 10px;");

		if (benzenoidSetPane.getSelectedBenzenoidPanes().size() == 0
				&& benzenoidSetPane.getParent().isSelectAllActivated())
			benzenoidSetPane.getParent().disableSelectAll();

		benzenoidSetPane.refreshCollectionProperties();
	}

	public void select() {

		benzenoidSetPane.setLock(true);

		benzenoidSetPane.addSelectedBenzenoidPane(this);
		isSelected = true;

		benzenoidSetPane.setDescription(buildDescription());
		benzenoidSetPane.setFrequencies(buildFrequencies());
		benzenoidSetPane.setEnergies(buildEnergies());
		benzenoidSetPane.setIntensities(buildEnergies());

		setStyle("-fx-border-color: blue;" + "-fx-border-width: 4;" + "-fx-border-radius: 10px;");
		benzenoidSetPane.refreshCollectionProperties();
	}

	public String getCSVLine(int index) {

		StringBuilder builder = new StringBuilder();

		builder.append(index + "\t");

		String[] splittedDescription = name.split(Pattern.quote("\n"));

		for (int i = 1; i < splittedDescription.length; i++) {

			String line = splittedDescription[i];
			String[] splittedLine = line.split(Pattern.quote(" = "));
			builder.append(splittedLine[1] + "\t");
		}

		return builder.toString();
	}

	public String getCSVHeader() {

		StringBuilder builder = new StringBuilder();

		builder.append("id_solution" + "\t");

		String[] splittedDescription = name.split(Pattern.quote("\n"));

		for (int i = 1; i < splittedDescription.length; i++) {

			String line = splittedDescription[i];
			String[] splittedLine = line.split(Pattern.quote(" = "));
			builder.append(splittedLine[0] + "\t");
		}

		return builder.toString();
	}

	public static void exportToCSV(ArrayList<ArrayList<BenzenoidPane>> allPanes, File file) {

		BufferedWriter writer;
		try {

			writer = new BufferedWriter(new FileWriter(file));

			for (ArrayList<BenzenoidPane> panes : allPanes) {

				String header = panes.get(0).getCSVHeader();
				writer.write(header + "\n");

				int index = 1;
				for (BenzenoidPane pane : panes) {

					String line = pane.getCSVLine(index);
					writer.write(line + "\n");

					index++;
				}

			}

			writer.close();

		} catch (IOException e1) {

			e1.printStackTrace();
		}

	}

	public BenzenoidSolution getBenzenoidSolution() {
		return benzenoidSolution;
	}

	public int[] getHexagonsCorrespondances() {
		return hexagonsCorrespondances;
	}

	public boolean isDrawMolecule() {
		return isDrawMolecule;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public ArrayList<Integer> getVerticesSolution() {
		return verticesSolution;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getNbCrowns() {
		return nbCrowns;
	}

	public BenzenoidCollectionPane getBenzenoidCollectionPane() {
		return benzenoidSetPane;
	}

	public String buildDescription() {

		if (description == null) {

			StringBuilder builder = new StringBuilder();

			Molecule molecule = benzenoidSetPane.getMolecule(index);

			builder.append(molecule.getNbNodes() + " carbons\n");
			builder.append(molecule.getNbHydrogens() + " hydrogens\n");
			if (molecule.getNbHexagons() == 1)
				builder.append(molecule.getNbHexagons() + " hexagon\n");
			else
				builder.append(molecule.getNbHexagons() + " hexagons\n");

			// String nbKekuleStructures =
			// Double.toString(molecule.getNbKekuleStructures()).split(Pattern.quote("."))[0];
			String kekuleStr = Double.toString(molecule.getNbKekuleStructures());

			String nbKekuleStructures = "";
			if (!kekuleStr.contains("E")) {
				nbKekuleStructures = Double.toString(molecule.getNbKekuleStructures()).split(Pattern.quote("."))[0];
			}

			else {
				nbKekuleStructures = kekuleStr;
			}

			if (molecule.getNbKekuleStructures() > 1.0)
				builder.append(new String(new String(nbKekuleStructures + " Kekulé structures").getBytes(),
						StandardCharsets.UTF_8) + "\n");
			else
				builder.append(new String(new String(nbKekuleStructures + " Kekulé structure").getBytes(),
						StandardCharsets.UTF_8) + "\n");

			Irregularity irregularity = molecule.getIrregularity();

			if (irregularity != null)
				builder.append(irregularity.toString() + "\n");
			else
				builder.append("XI = UNKNOWN");

			if (molecule.isAromaticitySet()) {
				Aromaticity aromaticity = molecule.getAromaticity();
				for (int i = 0; i < aromaticity.getLocalAromaticity().length; i++) {
					BigDecimal bd = new BigDecimal(aromaticity.getLocalAromaticity()[i]).setScale(2,
							RoundingMode.HALF_UP);
					builder.append("E(H_" + i + ")\t" + bd.doubleValue() + "\n");
				}
			}

			description = builder.toString();

			return description;

		}

		return description;
	}

	public void exportAsPNG(File file) {
		WritableImage wi = benzenoidDraw.snapshot(new SnapshotParameters(),
				new WritableImage((int) this.getWidth(), (int) this.getHeight()));
		BufferedImage awtImage = new BufferedImage((int) wi.getWidth(), (int) wi.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wi, awtImage), "png", file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Group getBenzenoidDraw() {
		return benzenoidDraw;
	}

	public void refreshRIType(RIType type) {
		if (benzenoidDraw instanceof AromaticityGroup) {
			((AromaticityGroup) benzenoidDraw).refreshRIType(type);
		}
	}

	public String buildFrequencies() {

		if (frequencies != null)
			return frequencies;

		if (getMolecule().databaseChecked()) {

			Molecule molecule = benzenoidSetPane.getMolecule(index);
			ResultLogFile log = molecule.getNicsResult();

			StringBuilder b = new StringBuilder();

			if (log != null) {
				int i = 0;
				for (Double frequencie : log.getFrequencies()) {
					b.append(i + "\t" + frequencie + "\n");
					i++;
				}
			}

			else
				b.append("Unknown");

			frequencies = b.toString();

		}

		else {
			return "Unknown";
		}

		return frequencies;
	}

	public String buildEnergies() {

		if (energies != null)
			return energies;

		if (getMolecule().databaseChecked()) {

			Molecule molecule = benzenoidSetPane.getMolecule(index);
			ResultLogFile log = molecule.getNicsResult();

			StringBuilder b = new StringBuilder();

			if (log != null) {
				int i = 0;
				for (Double energy : log.getFinalEnergy()) {
					b.append(i + "\t" + energy + "\n");
					i++;
				}
			}

			else
				b.append("Unknown");

			energies = b.toString();

		}

		else
			return "Unknown";
		return energies;
	}

	public String buildIntensities() {

		if (intensities != null)
			return intensities;

		if (getMolecule().databaseChecked()) {

			Molecule molecule = benzenoidSetPane.getMolecule(index);
			ResultLogFile log = molecule.getNicsResult();

			StringBuilder b = new StringBuilder();

			if (log != null) {
				int i = 0;
				for (Double intensity : log.getIntensities()) {
					b.append(i + "\t" + intensity + "\n");
					i++;
				}
			}

			else
				b.append("Unknown");

			intensities = b.toString();
		}

		else
			return "Unknown";
		return intensities;

	}

	public HBox getDescriptionBox() {
		return descriptionBox;
	}

	public Molecule getMolecule() {
		return benzenoidSetPane.getMolecule(index);
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(BenzenoidPane o) {
		if (index < o.getIndex())
			return -1;
		else if (index == o.getIndex())
			return 0;
		else
			return 1;
	}
}
