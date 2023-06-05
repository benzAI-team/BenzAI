package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;

import classifier.Irregularity;
import benzenoid.Benzenoid;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Utils;

public class InsertMolecules {

	private Connection connect = null;
	private final Statement statement = null;
	private final ResultSet resultSet = null;

	private void insert(String graphFile, String logFile, int index) throws Exception {

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
				BigDecimal bd = new BigDecimal(e).setScale(3, RoundingMode.HALF_UP);
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

            String insertMolecule = "INSERT INTO molecule (id_molecule, irregularity, molecule_name, nb_carbons, nb_hexagons, nb_hydrogens)\n" +
                    "\tVALUES (" + index + ", " + irregbd.doubleValue() + ", '" + name + "', " + molecule.getNbNodes()
                    + ", " + molecule.getNbHexagons() + ", " + molecule.getNbHydrogens() + ");";

            String insertResult = "INSERT INTO gaussian_result (final_energies, frequencies, id_molecule, intensities, zero_point_energy)\n" +
                    "\tVALUES ('" + finalEnergies + "', '" + frequencies + "', " + index + ", '"
                    + intensities + "', '" + log.getZeroPointEnergy() + "');";

			// statement = connect.createStatement();
			// statement.executeUpdate(insertMolecule.toString());

			// statement = connect.createStatement();
			// statement.executeUpdate(insertResult.toString());

			w.write(insertMolecule + "\n");
			w.write(insertResult + "\n");
			w.write("\n");

			w.close();

		}
	}

	public void insertAll() {

		try {

			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/benzenoids?" + "user=root&password=root");

			BufferedReader r = new BufferedReader(new FileReader(new File(
					"C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\graph_files\\list_graph_files.txt")));
			String line;

			int i = 0;

			while ((line = r.readLine()) != null) {

				String[] splittedLine = line.split(Pattern.quote("\\"));
				String[] ss = splittedLine[splittedLine.length - 1].split(Pattern.quote("."));

				String moleculeName = ss[0];

				String logName = "C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\log_files\\" + moleculeName
						+ ".log";

				insert(line, logName, i);
				i++;
			}

			r.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			close();
		}

	}

	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

	public static void main(String[] args) {
		InsertMolecules i = new InsertMolecules();
		i.insertAll();
	}
}
