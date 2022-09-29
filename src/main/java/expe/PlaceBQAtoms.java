package expe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import molecules.Molecule;
import parsers.GraphParser;
import utils.Couple;
import utils.Triplet;
import utils.Utils;

public class PlaceBQAtoms {

	private static void usage() {
		System.out.println("usage java -jar file.jar f1.graph_coord f2.mxyz");
	}
	
	private static Couple<ArrayList<Triplet<Double, Double, Double>>, ArrayList<Triplet<Double, Double, Double>>> readMXYZ(File mxyz) throws IOException {
		
		ArrayList<String> lines = new ArrayList<>();
		BufferedReader r = new BufferedReader(new FileReader(mxyz));
		String line;
		
		int i = 0;
		
		while((line = r.readLine()) != null) {
			if (i >= 2)
				lines.add(line);
			i++;
		}
		
		r.close();
		
		ArrayList<Triplet<Double, Double, Double>> carbons = new ArrayList<>();
		ArrayList<Triplet<Double, Double, Double>> hydrogens = new ArrayList<>();
		
		for (int index = 0 ; index < lines.size() - 1 ; index++) {
			String l = lines.get(index);
			String [] sl = Utils.splitBySeparators(l);
			
			String atom = sl[0];
			
			Double x = Double.parseDouble(sl[1]);
			Double y = Double.parseDouble(sl[2]);
			Double z = Double.parseDouble(sl[3]);
			
			if (atom.equals("C"))
				carbons.add(new Triplet<>(x, y, z));
			else if (atom.equals("H"))
				hydrogens.add(new Triplet<>(x, y, z));
		}
		
		return new Couple<>(carbons, hydrogens);
	}
	
	public static void main(String [] args) throws IOException {
		
//		if (args.length != 2)
//			usage();
//		
//		else {
			
			String name = "unknown_molecule_6";
		
			String a0 = "/home/adrien/Bureau/test_fleches/" + name + ".graph";
			String a1 = "/home/adrien/Bureau/test_fleches/" + name + ".mxyz";
			String a2 = "/home/adrien/Bureau/test_fleches/" + name + ".coords";
			
			Molecule molecule = GraphParser.parseUndirectedGraph(new File(a0));
			Couple<ArrayList<Triplet<Double, Double, Double>>, ArrayList<Triplet<Double, Double, Double>>> atoms = readMXYZ(new File(a1));
			ArrayList<Triplet<Double, Double, Double>> carbons = atoms.getX();
			ArrayList<Triplet<Double, Double, Double>> hydrogens = atoms.getX();
			
			ArrayList<Triplet<Double, Double, Double>> bq = new ArrayList<>();
			
			int [][] hexagons = new int[molecule.getNbHexagons()][6];
			BufferedReader r = new BufferedReader(new FileReader(new File(a2)));
			String line;
			
			int li = 0;
			while((line = r.readLine()) != null) {
				String [] sl = line.split(" ");
				for (int i = 0 ; i < sl.length ; i++)
					hexagons[li][i] = Integer.parseInt(sl[i]);
				li++;
			}
			
			r.close();
			
			for (int i = 0 ; i < molecule.getNbHexagons() ; i++) {
				
				int [] hexa = hexagons[i];
				
				Triplet<Double, Double, Double> c1 = carbons.get(hexa[0]); //0
				Triplet<Double, Double, Double> c2 = carbons.get(hexa[1]); //1
				Triplet<Double, Double, Double> c3 = carbons.get(hexa[2]); //3
				Triplet<Double, Double, Double> c4 = carbons.get(hexa[3]); //4
				Triplet<Double, Double, Double> c5 = carbons.get(hexa[4]); //6
				Triplet<Double, Double, Double> c6 = carbons.get(hexa[5]); //2
				
				double x = (c1.getX() + c2.getX() + c3.getX() + c4.getX() + c5.getX() + c6.getX()) / 6.0;
				double y = (c1.getY() + c2.getY() + c3.getY() + c4.getY() + c5.getY() + c6.getY()) / 6.0;				
				double z = (c1.getZ() + c2.getZ() + c3.getZ() + c4.getZ() + c5.getZ() + c6.getZ()) / 6.0;
				
				bq.add(new Triplet<>(x, y, z));
				
//				System.out.println(" C " + c1.getX() + " " + c1.getY() + c1.getZ());
//				System.out.println(" C " + c2.getX() + " " + c2.getY() + c2.getZ());
//				System.out.println(" C " + c3.getX() + " " + c3.getY() + c3.getZ());
//				System.out.println(" C " + c4.getX() + " " + c4.getY() + c4.getZ());
//				System.out.println(" C " + c5.getX() + " " + c5.getY() + c5.getZ());
//				System.out.println(" C " + c6.getX() + " " + c6.getY() + c6.getZ());
//				
//				System.out.println(" Bq " + x + " " + y + " " + z);
			}
			
			for (Triplet<Double, Double, Double> c : carbons)
				System.out.println(" C " + c.getX() + " " + c.getY() + " " + c.getZ());
			
			for (Triplet<Double, Double, Double> c : hydrogens)
				System.out.println(" H " + c.getX() + " " + c.getY() + " " + c.getZ());
			
			for (Triplet<Double, Double, Double> c : bq)
				System.out.println(" Bq " + c.getX() + " " + c.getY() + " " + c.getZ());
			
			
			
		//}
			
	
	}
	
}