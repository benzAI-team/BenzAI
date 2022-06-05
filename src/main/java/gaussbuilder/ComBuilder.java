package gaussbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import molecules.Molecule;
import parsers.GraphParser;
import parsers.ComConverter.ComType;
import utils.Triplet;

public class ComBuilder {

	public static void buildComFile(Geometry geometry, String outputFilePath, int nbElectronsDiff, ComType type, String title) throws IOException {
		
		Triplet<Double, Double, Double> [] carbons = geometry.getCarbons();
		ArrayList<Triplet<Double, Double, Double>> hydrogens = geometry.getHydrogens();
		
		/*
		 * ligne multiplicit� : charge " " spin charge : +1 si on enl�ve un electron
		 */
		int nbCarbons = carbons.length;
		int nbHydrogens = hydrogens.size();
		int spin = (6 * nbCarbons) + nbHydrogens + nbElectronsDiff;
		int charge = -1 * nbElectronsDiff;

		File outputFile = new File(outputFilePath);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

		switch (type) {

		case ER:
			writer.write("%mem=1Gb" + "\n");
			writer.write("# opt b3lyp/6-31G" + "\n");
			writer.write("\n");
			writer.write(title + "\n");
			writer.write("\n");
			if (spin % 2 == 0)
				writer.write(charge + " 1" + "\n");
			else
				writer.write(charge + " 2" + "\n");
			break;

		case IR:
			writer.write("%nproc=32" + "\n");
			writer.write("%chk=" + title + ".chk" + "\n");
			writer.write("%mem=1Gb" + "\n");
			writer.write("# b3lyp/6-31g opt freq" + "\n");
			writer.write("\n");
			writer.write(title + "\n");
			writer.write("\n");

			if (spin % 2 == 0)
				writer.write(charge + " 1" + "\n");
			else
				writer.write(charge + " 2" + "\n");
			break;
		}

		String s = "";

		for (Triplet<Double, Double, Double> carbon : carbons) {
			writer.write(" C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() + "\n");
			s += " C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() + "\n";
		}

		for (Triplet<Double, Double, Double> hydrogen : hydrogens) {
			writer.write(" H " + hydrogen.getX() + " " + hydrogen.getY() + " " + hydrogen.getZ() + "\n");
			s += " H " + hydrogen.getX() + " " + hydrogen.getY() + " " + hydrogen.getZ() + "\n";
		}

		writer.write("\n");

		writer.close();
		
	}
	
	public static void main(String [] args) throws IOException {
		File dir = new File("/home/adrien/Documents/old_log_files/bad_benzenoids/");
		
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".graph_coord")) {
				Molecule m = GraphParser.parseUndirectedGraph(f);
				Geometry geometry = GeometryBuilder.buildGeometry(m);
				ComBuilder.buildComFile(geometry, f.getAbsolutePath().replace(".graph_coord", ".com"), 0, ComType.ER, f.getName().replace(".graph_coord", ""));
				AmpacBuilder.buildAmpacFile(m, f.getAbsolutePath().replace(".graph_coord", ".dat"));
			}
		}
	}
}
