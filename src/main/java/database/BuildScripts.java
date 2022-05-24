package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Pattern;

import molecules.AtomGeometry;
import molecules.Molecule;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Triplet;

public class BuildScripts {

	private static String directory;
	private static BufferedWriter writer;

	private static long indexNames;
	private static long indexIms2d;
	private static long indexPoint;

	private static void initialize() throws IOException {

		directory = "";
		writer = new BufferedWriter(new FileWriter(new File(directory + "/insert.sql")));
		indexNames = 0;
		indexIms2d = 0;
		indexPoint = 0;
	}

	private static void insertBenzenoid(long id, Molecule molecule) throws IOException {

		BigDecimal irregbd;

		ArrayList<String> names = molecule.getNames();

		if (molecule.getIrregularity() != null)
			irregbd = new BigDecimal(molecule.getIrregularity().getXI()).setScale(3, RoundingMode.HALF_UP);
		else
			irregbd = new BigDecimal(-1.0).setScale(3, RoundingMode.HALF_UP);

		writer.write("INSERT INTO benzenoid (id, irregularity, nbCarbons, nbHexagons, nbHydrogens)\n");
		writer.write("\tVALUES (" + id + ", " + irregbd.doubleValue() + ", " + molecule.getNbNodes() + ", "
				+ molecule.getNbHexagons() + ", " + molecule.getNbHydrogens() + ");\n\n");

		for (String moleculeName : names) {

			writer.write("INSERT INTO name (idName, idMolecule, name)\n");
			writer.write("\tVALUES(" + indexNames + ", " + id + ", '" + moleculeName + "');\n");

			indexNames++;
		}

		writer.write("\n");

	}

	private static void insertIRSpectra(long id, File irFile, File graphFile) throws IOException {

		ResultLogFile log = SpectrumsComputer.parseLogFile(irFile.getAbsolutePath());

		Double e = log.getFinalEnergy().get(log.getFinalEnergy().size() - 1);
		BigDecimal bd = new BigDecimal(e).setScale(8, RoundingMode.HALF_UP);
		double finalEnergy = bd.doubleValue();

		StringBuilder frequencies = new StringBuilder();
		for (int i = 0; i < log.getFrequencies().size(); i++) {
			Double freq = log.getFrequencies().get(i);

			if (freq < 0.0)
				System.out.println(graphFile.getName() + " : frequence negative");

			BigDecimal bdFreq = new BigDecimal(freq).setScale(3, RoundingMode.HALF_UP);
			frequencies.append(bdFreq.doubleValue() + " ");

			if (i < log.getFrequencies().size() - 1)
				frequencies.append(" ");
		}

		StringBuilder intensities = new StringBuilder();
		for (int i = 0; i < log.getIntensities().size(); i++) {
			Double inten = log.getIntensities().get(i);
			BigDecimal bdInten = new BigDecimal(inten).setScale(3, RoundingMode.HALF_UP);
			intensities.append(bdInten.doubleValue());

			if (i < log.getIntensities().size() - 1)
				intensities.append(" ");
		}

		if (frequencies.toString().equals("") || intensities.toString().equals(""))
			System.out.print("");

		writer.write(
				"INSERT INTO gaussian_result (final_energies, frequencies, id_molecule, intensities, zero_point_energy)\n");
		writer.write("\tVALUES (" + finalEnergy + ", '" + frequencies + "', " + id + ", '" + intensities + "', '"
				+ log.getZeroPointEnergy() + "');\n\n");

	}

	private static void insertIMS2D(long id, File ims2dFile) throws IOException {

		ArrayList<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(ims2dFile));
		String line;

		while ((line = reader.readLine()) != null)
			lines.add(line);

		reader.close();

		int lineIndex = 0;

		ArrayList<AtomGeometry> atoms = new ArrayList<>();
		Triplet<Double, Double, Double> origin;
		Triplet<Double, Double, Double> vector1;
		Triplet<Double, Double, Double> vector2;

		line = null;

		while (line != "") {
			line = lines.get(lineIndex);

			String[] sl = line.split(" ");

			String label = sl[0];
			double x = Double.parseDouble(sl[1]);
			double y = Double.parseDouble(sl[2]);
			double z = Double.parseDouble(sl[3]);

			atoms.add(new AtomGeometry(label, x, y, z));
			lineIndex++;
		}

		lineIndex++;

		String[] originSplit = lines.get(lineIndex).split(" ");

		double xOrigin = Double.parseDouble(originSplit[1]);
		double yOrigin = Double.parseDouble(originSplit[2]);
		double zOrigin = Double.parseDouble(originSplit[3]);
		origin = new Triplet<>(xOrigin, yOrigin, zOrigin);

		lineIndex += 2;

		String[] vector1Split = lines.get(lineIndex).split(" ");

		double xVector1 = Double.parseDouble(vector1Split[1]);
		double yVector1 = Double.parseDouble(vector1Split[2]);
		double zVector1 = Double.parseDouble(vector1Split[3]);
		vector1 = new Triplet<>(xVector1, yVector1, zVector1);

		lineIndex++;

		String[] vector2Split = lines.get(lineIndex).split(" ");

		double xVector2 = Double.parseDouble(vector2Split[1]);
		double yVector2 = Double.parseDouble(vector2Split[2]);
		double zVector2 = Double.parseDouble(vector2Split[3]);
		vector2 = new Triplet<>(xVector2, yVector2, zVector2);

	}

	private static void treatMolecule(long id, File graphFile, File irFile, File nicsFile, File ims2dFile)
			throws IOException {

		String moleculeName = graphFile.getName().split(Pattern.quote("."))[0];
		writer.write("# " + moleculeName + "\n\n");

		Molecule molecule = GraphParser.parseUndirectedGraph(graphFile);

		insertBenzenoid(id, molecule);
		insertIRSpectra(id, irFile, graphFile);
	}

	private static void treatMolecules() throws IOException {

		File dir = new File(directory);
		File[] files = dir.listFiles();
		long id = 0;

		for (File molFile : files) {
			if (molFile.getName().endsWith(".graph_coord")) {

				File irFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".log"));
				File nicsFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".nics"));
				File ims2dFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".ims2d"));

				treatMolecule(id, molFile, irFile, nicsFile, ims2dFile);

				id++;
			}
		}
	}

	public static void main(String[] args) throws IOException {

		initialize();
		treatMolecules();

		writer.close();
	}
}
