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
import utils.Triplet;
import utils.Utils;


public enum NewFormat {
    ;

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
				if (molecule.getEdgeMatrix()[carbonIndex][i] == 1) {
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
	
	public static void buildCom(String inputXYZ, String outputCom) throws IOException {
	
		BufferedReader r = new BufferedReader(new FileReader(new File(inputXYZ)));
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputCom)));
		
//		w.write("%mem=1Gb" + "\n");
//		w.write("# opt b3lyp/6-31G" + "\n");
//		w.write("\n");
//		w.write(title + "\n");
//		w.write("\n");
//		if (spin % 2 == 0)
//			w.write(charge + " 1" + "\n");
//		else
//			w.write(charge + " 2" + "\n");
		
		String line;
		boolean first = true;
		
		ArrayList<String> lines = new ArrayList<>();
		
		int nbCarbons = 0;
		int nbHydrogens = 0;
		
		while((line = r.readLine()) != null) {
			if (first)
				first = false;
			else {
				lines.add(line);
				
				String [] split = Utils.splitBySeparators(line);
				String atom = split[2];
				if ("C".equals(atom))
					nbCarbons ++;
				else if ("H".equals(atom))
					nbHydrogens ++;
			}
		}
		
		int spin = (6 * nbCarbons) + nbHydrogens;
		int charge = 0;
		
		w.write("%mem=1Gb" + "\n");
		w.write("# opt b3lyp/6-31G" + "\n");
		w.write("\n");
		w.write(inputXYZ + "\n");
		w.write("\n");
		if (spin % 2 == 0)
			w.write(charge + " 1" + "\n");
		else
			w.write(charge + " 2" + "\n");
		
		for (String l : lines) {
			
			String [] split = Utils.splitBySeparators(l);
			
			String atom = split[2];
			String x = split[3];
			String y = split[4];
			String z = split[5];
			
			w.write(" " + atom + "\t" + x + "\t" + y + "\t" + z + "\n");
		}
		
		w.write("\n");
		
		w.close();
		r.close();
	}

	public static void main(String[] args) throws IOException {
		//Molecule m = GraphParser
		//		.parseUndirectedGraph(new File("/home/adrien/Documents/old_log_files/bad_benzenoids/7_hexagons202.graph_coord"));

		//generate(m, "/home/adrien/Documents/old_log_files/bad_benzenoids/7_hexagons202.xyz");
		
		File dir = new File("/home/adrien/Documents/old_log_files/bad_benzenoids/");
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".xyz_2")) {
				System.out.println("Treating " + f.getAbsolutePath());
				//Molecule m = GraphParser.parseUndirectedGraph(new File(f.getAbsolutePath().replace(".xyz_2", ".graph_coord")));
				//generate(m, f.getAbsolutePath().replace(".graph_coord", ".xyz"));
				buildCom(f.getAbsolutePath(), f.getAbsolutePath().replace(".xyz_2", "_tinker.com"));
			}
		}
		
		
		
		
		
	}
}
