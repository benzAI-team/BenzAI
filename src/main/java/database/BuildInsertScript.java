package database;

import classifier.Irregularity;
import benzenoid.Benzenoid;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Utils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public enum BuildInsertScript {
    ;

    private static void readFile(String graphFile, String logFile, int index) throws IOException {

		if (!"C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\graph_files\\benzene.graph_coord"
				.equals(graphFile)) {

			System.out.println("Treating : " + graphFile);

			Benzenoid molecule = GraphParser.parseUndirectedGraph(graphFile, null, false);
			ResultLogFile log = SpectrumsComputer.parseLogFile(logFile);

			Irregularity irregularity = Utils.computeParameterOfIrregularity(molecule);
			BigDecimal irregbd = BigDecimal.valueOf(irregularity.getXI()).setScale(3, RoundingMode.HALF_UP);

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

			w.write("INSERT INTO benzenoid (id, irregularity, name, nbCarbons, nbHexagons, nbHydrogens)\n");
			w.write("\tVALUES (" + index + ", " + irregbd.doubleValue() + ", '" + name + "', " + molecule.getNbCarbons()
					+ ", " + molecule.getNbHexagons() + ", " + molecule.getNbHydrogens() + ");\n");

			w.write("INSERT INTO gaussian_result (final_energies, frequencies, id_molecule, intensities, zero_point_energy)\n");
			w.write("\tVALUES ('" + finalEnergies + "', '" + frequencies + "', " + index + ", '" + intensities + "', '"
					+ log.getZeroPointEnergy() + "');\n\n");

			w.close();

		}
	}

	private static void treatAll() throws IOException {

		BufferedReader r = new BufferedReader(new FileReader(
				new File("C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\graph_files\\list_graph_files.txt")));
		String line;

		int i = 0;

		while ((line = r.readLine()) != null) {

			String[] splittedLine = line.split(Pattern.quote("\\"));
			String[] ss = splittedLine[splittedLine.length - 1].split(Pattern.quote("."));

			String moleculeName = ss[0];

			String logName = "C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\new_results\\" + moleculeName
					+ "_irreg.log";

			readFile(line, logName, i);
			i++;
		}

		r.close();
	}

	public static void main(String[] args) throws IOException {
		treatAll();
	}
}
