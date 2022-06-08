package gaussbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import molecules.Molecule;
import parsers.GraphParser;
import utils.Triplet;

public class NewFormat {

	public static void generate(Molecule molecule, String outputPath) throws IOException {
		Geometry geometry = GeometryBuilder.buildGeometry(molecule);
		File f = new File(outputPath);
		BufferedWriter w = new BufferedWriter(new FileWriter(f));

		int nbAtoms = geometry.getCarbons().length + geometry.getHydrogens().size();
		w.write(nbAtoms + "\n");

		int index = 1;
		for (Triplet<Double, Double, Double> carbon : geometry.getCarbons()) {
			w.write(index + " " + "C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() + " 2 ");

			int carbonIndex = index - 1;

			for (int i = 0; i < molecule.getNbNodes(); i++) {
				if (molecule.getAdjacencyMatrix()[carbonIndex][i] == 1) {
					w.write((i + 1) + " ");
				}
			}

			for (int i = 0; i < molecule.getNbHydrogens(); i++) {
				if (geometry.getHydrogensConnections().get(i) == carbonIndex)
					w.write((i + molecule.getNbNodes() + 1) + " ");
			}

			w.write("\n");

			index++;
		}

		for (int i = 0; i < molecule.getNbHydrogens(); i++) {
			Triplet<Double, Double, Double> hydrogen = geometry.getHydrogens().get(i);
			w.write(index + " " + "H " + hydrogen.getX() + " " + hydrogen.getY() + " " + hydrogen.getZ() + " 5 "
					+ (geometry.getHydrogensConnections().get(i)/* + molecule.getNbNodes() */ + 1) + "\n");
			index++;
		}

		w.close();
	}

	public static void main(String[] args) throws IOException {
		//Molecule m = GraphParser
		//		.parseUndirectedGraph(new File("/home/adrien/Documents/old_log_files/bad_benzenoids/7_hexagons202.graph_coord"));

		//generate(m, "/home/adrien/Documents/old_log_files/bad_benzenoids/7_hexagons202.xyz");
		
		File dir = new File("/home/adrien/Documents/old_log_files/bad_benzenoids/");
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".graph_coord")) {
				System.out.println("Treating " + f.getAbsolutePath());
				Molecule m = GraphParser.parseUndirectedGraph(f);
				generate(m, f.getAbsolutePath().replace(".graph_coord", ".xyz"));
			}
		}
		
	}
}
