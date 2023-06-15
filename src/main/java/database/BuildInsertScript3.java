package database;

import classifier.Irregularity;
import benzenoid.Benzenoid;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public enum BuildInsertScript3 {
    ;

    public static void main(String [] args) throws IOException {
		
		File dir = new File("/home/adrien/Documents/old_log_files");
		File [] files = dir.listFiles();
		int index = 0;
		
		int indexNames = 0;
		
		for (File molFile : files) {
			if (molFile.getName().endsWith(".graph_coord")) {
				
				System.out.println("Treating " + molFile.getName());
				
				File logFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".log"));
				
				Benzenoid molecule = GraphParser.parseUndirectedGraph(molFile);
				ResultLogFile log = SpectrumsComputer.parseLogFile(logFile.getAbsolutePath());
				
				Irregularity irregularity = Utils.computeParameterOfIrregularity(molecule);
				BigDecimal irregbd;
				
				ArrayList<String> names = molecule.getNames();
				
				if (irregularity != null)
					irregbd = BigDecimal.valueOf(irregularity.getXI()).setScale(3, RoundingMode.HALF_UP);
				else
					irregbd = BigDecimal.valueOf(-1.0).setScale(3, RoundingMode.HALF_UP);
				
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

					if (e < 0.0)
						System.out.println(molFile.getName() + " : frequence negative");
					
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

				if ("".contentEquals(frequencies) || "".contentEquals(intensities))
					System.out.print("");
				
				BufferedWriter w = new BufferedWriter(new FileWriter(
						new File(dir.getAbsolutePath() + "/insert.sql"), true));

				String name2 = molFile.getName().replace(".graph_coord", "");
				
				if (index == 6405)
					System.out.print("");
					
				w.write("INSERT INTO benzenoid (id, irregularity, name, nbCarbons, nbHexagons, nbHydrogens)\n");
				w.write("\tVALUES (" + index + ", " + irregbd.doubleValue() + ", '" + name + "', " + molecule.getNbCarbons()
						+ ", " + molecule.getNbHexagons() + ", " + molecule.getNbHydrogens() + ");\n");

				w.write("INSERT INTO gaussian_result (final_energies, frequencies, id_molecule, intensities, zero_point_energy)\n");
				w.write("\tVALUES ('" + finalEnergies + "', '" + frequencies + "', " + index + ", '" + intensities + "', '"
						+ log.getZeroPointEnergy() + "');\n\n");

				for (String moleculeName : names) {
					
					w.write("INSERT INTO name (idName, idMolecule, name)\n");
					w.write("\tVALUES(" + indexNames + ", " + index + ", '" + moleculeName + "');\n");
					
					indexNames ++;
				}
				
				w.close();
				
				index ++;
			}
		}
	}
}
