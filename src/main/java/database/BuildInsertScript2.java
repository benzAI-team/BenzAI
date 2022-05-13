package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import classifier.Irregularity;
import molecules.Molecule;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Utils;

public class BuildInsertScript2 {

	private static void buildScript(File graphFilesDirectory, File logFilesDirectory) throws IOException {

		File[] graphFiles = graphFilesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".graph_coord");
			}
		});

		for (int index = 0; index < graphFiles.length; index++) {

			File graphFile = graphFiles[index];

			System.out.println("treating " + graphFile);

			String logFileName = logFilesDirectory.getAbsolutePath() + graphFile.getName().split(Pattern.quote("."))[0]
					+ ".log";
			File logFile = new File(logFileName);

			Molecule molecule = GraphParser.parseUndirectedGraph(graphFile.getAbsolutePath(), null, false);
			ResultLogFile log = SpectrumsComputer.parseLogFile(logFile.getAbsolutePath());

			Irregularity irregularity = Utils.computeParameterOfIrregularity(molecule);
			BigDecimal irregbd = new BigDecimal(irregularity.getXI()).setScale(3, RoundingMode.HALF_UP);

			String name = molecule.toString();

			StringBuilder finalEnergies = new StringBuilder();
			for (int i = 0; i < log.getFinalEnergy().size(); i++) {
				Double e = log.getFinalEnergy().get(i);
				BigDecimal bd = new BigDecimal(e).setScale(8, RoundingMode.HALF_UP);
				finalEnergies.append(bd.doubleValue());

				if (i < log.getFinalEnergy().size() - 1)
					finalEnergies.append(" ");
			}

			StringBuilder frequencies = new StringBuilder();
			for (int i = 0; i < log.getFrequencies().size(); i++) {
				Double e = log.getFrequencies().get(i);
				BigDecimal bd = new BigDecimal(e).setScale(3, RoundingMode.HALF_UP);
				frequencies.append(bd.doubleValue() + " ");

				if (i < log.getFrequencies().size() - 1)
					frequencies.append(" ");
			}

			StringBuilder intensities = new StringBuilder();
			for (int i = 0; i < log.getIntensities().size(); i++) {
				Double e = log.getIntensities().get(i);
				BigDecimal bd = new BigDecimal(e).setScale(3, RoundingMode.HALF_UP);
				intensities.append(bd.doubleValue());

				if (i < log.getIntensities().size() - 1)
					intensities.append(" ");
			}

			BufferedWriter w = new BufferedWriter(new FileWriter(
					new File("C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\insert.sql"), true));

			w.write("INSERT INTO molecule (id_molecule, irregularity, molecule_name, nb_carbons, nb_hexagons, nb_hydrogens)\n");
			w.write("\tVALUES (" + index + ", " + irregbd.doubleValue() + ", '" + name + "', " + molecule.getNbNodes()
					+ ", " + molecule.getNbHexagons() + ", " + molecule.getNbHydrogens() + ");\n");

			w.write("INSERT INTO gaussian_result (final_energies, frequencies, id_molecule, intensities, zero_point_energy)\n");
			w.write("\tVALUES ('" + finalEnergies + "', '" + frequencies + "', " + index + ", '" + intensities + "', '"
					+ log.getZeroPointEnergy() + "');\n\n");

			w.close();

		}
	}

	public static void main(String[] args) throws IOException {

		File graphDirectory = new File("C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\graph_files");
		File logDirectory = new File("C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\new_results");

		buildScript(graphDirectory, logDirectory);
	}
}
