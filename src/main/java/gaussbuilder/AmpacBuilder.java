package gaussbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import molecules.Molecule;
import utils.Triplet;

public class AmpacBuilder {

	public static void buildAmpacFile(Molecule molecule, String outputFilePath) throws IOException {
		
		Geometry geometry = GeometryBuilder.buildGeometry(molecule);
		Triplet<Double, Double, Double> [] carbons = geometry.getX();
		ArrayList<Triplet<Double, Double, Double>> hydrogens = geometry.getY();
		
		File outputFile = new File(outputFilePath);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		writer.write("  am1 rhf singlet truste t=auto\n");
		writer.write(outputFile.getName() + "\n");
		writer.write(" Comment\n");
		
		//" C             -1.214500  1    0.694000  1    0.000000  1 #   "
		for (int i = 0 ; i < carbons.length ; i++) {
			
			Triplet<Double, Double, Double> carbon = carbons[i];
			double x = carbon.getX();
			double y = carbon.getY();
			double z = carbon.getZ();
			
			writer.write(" C\t\t" + x + "\t\t1\t\t" + y + "\t\t1\t\t" + z + "\t\t1 #   \n");
		}
		
		for (Triplet<Double, Double, Double> hydrogen : hydrogens) {
			double x = hydrogen.getX();
			double y = hydrogen.getY();
			double z = hydrogen.getZ();
			
			writer.write(" H\t\t" + x + "\t\t1\t\t" + y + "\t\t1\t\t" + z + "\t\t1 #   \n");
		}
		
		writer.write(" 0\t\t" + 0.000000 + "\t\t0\t\t" + 0.000000 + "\t\t0\t\t" + 0.000000 + "\t0\n");
		
		writer.close();
	}
	
	public static Geometry parseAmpacGeometry(String inputFilePath) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputFilePath)));
		String line;
		ArrayList<String> lines = new ArrayList<>();
		
		while(true) {
			
			line = reader.readLine();
			if (line == null)
				break;
			
			if (line.contains("CARTESIAN COORDINATES")) {
				lines = new ArrayList<>();
				
				while(!line.equals("")) {
					lines.add(line);
					line = reader.readLine();
				}
				
			}
		}
		
		int nbCarbons = 0;
		
		for (int i = 2 ; i < lines.size() ; i++) {
			line = lines.get(i);
			String [] splittedString = line.split("\\s+");
			
			if (splittedString[2].equals("C"))
				nbCarbons ++;
			
		}
		
		Triplet<Double, Double, Double> [] carbons = new Triplet[nbCarbons];
		ArrayList<Triplet<Double, Double, Double>> hydrogens = new ArrayList<>();
		
		int indexCarbon = 0;
		
		for (int i = 2 ; i < lines.size() ; i++) {
			line = lines.get(i);
			String [] splittedString = line.split("\\s+");
			
			double x = Double.parseDouble(splittedString[3]);
			double y = Double.parseDouble(splittedString[4]);
			double z = Double.parseDouble(splittedString[5]);
			
			if (splittedString[2].equals("C")) {
				carbons[indexCarbon] = new Triplet<>(x, y, z);
				indexCarbon ++;
			}
			
			else
				hydrogens.add(new Triplet<>(x, y, z));
			
		}
		
		reader.close();
		return new Geometry(carbons, hydrogens);
	}
}
