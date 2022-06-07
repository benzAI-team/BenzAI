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
		BufferedWriter w = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/bad_benzenoids/delete_bad_benzenodis.sql")));
		
//		StringBuilder benzenoid = new StringBuilder();
//		StringBuilder point = new StringBuilder();
//		StringBuilder ims2d1a = new StringBuilder();
//		StringBuilder ir = new StringBuilder();
//		StringBuilder names = new StringBuilder();
//		
//		for (File f : dir.listFiles()) {
//			if (f.getName().endsWith(".graph_coord")) {
//				Molecule m = GraphParser.parseUndirectedGraph(f);
////				Geometry geometry = GeometryBuilder.buildGeometry(m);
////				ComBuilder.buildComFile(geometry, f.getAbsolutePath().replace(".graph_coord", ".com"), 0, ComType.ER, f.getName().replace(".graph_coord", ""));
////				AmpacBuilder.buildAmpacFile(m, f.getAbsolutePath().replace(".graph_coord", ".dat"));
//				String name = m.getNames().get(0);
//				benzenoid.append("DELETE FROM benzenoid WHERE id IN (SELECT idMolecule FROM name WHERE name = '" + name + "');\n");
//				point.append("DELETE FROM point_ims2d_1a WHERE idIms2d1a IN "
//						+ "(SELECT idIms2d1a FROM ims2d_1a WHERE idBenzenoid IN (SELECT id FROM benzenoid WHERE id IN (SELECT idMolecule FROM name WHERE name = '" + name + "')));\n");
//				ims2d1a.append("DELETE FROM ims2d_1a WHERE idBenzenoid IN (SELECT id from benzenoid WHERE id IN (SELECT idMolecule FROM name WHERE name = '" + name + "'));\n");
//				ir.append("DELETE FROM gaussian_result WHERE id_molecule IN (SELECT id from benzenoid WHERE id IN (SELECT idMolecule FROM name WHERE name = '" + name + "'));\n");
//				names.append("DELETE FROM name WHERE idMolecule IN (SELECT id FROM benzenoid WHERE id IN (SELECT idMolecule FROM name WHERE name = '" + name + "'));\n");
//			}
//			
//		}
//
//		System.out.println(ir.toString().split("\n")[0]);
//		System.out.println(point.toString().split("\n")[0]);
//		System.out.println(ims2d1a.toString().split("\n")[0]);
//		System.out.println(names.toString().split("\n")[0]);
//		System.out.println(benzenoid.toString().split("\n")[0]);
//		
//		w.write(ir.toString() + "\n" + point.toString() + "\n" + ims2d1a.toString() + "\n" + names.toString() + benzenoid.toString());
//		
//		w.close();
		
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".out")) {
				Geometry g = AmpacBuilder.parseAmpacGeometry(f.getAbsolutePath());
				//buildComFile(g, f.getAbsolutePath().replace(".out", "_ampac.com"), 0, ComType.IR, f.getName());
				Molecule m = GraphParser.parseUndirectedGraph(new File(f.getAbsolutePath().replace(".out", ".graph_coord")));
				GaussChecker.checkGeometry(f.getName(), m, g);
			}
		}
	}
}
