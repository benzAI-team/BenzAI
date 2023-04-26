package gaussbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
		
		int nbElectrons = carbons.length;
		
		if (nbElectrons % 2 == 0)
			writer.write("  am1 rhf singlet truste t=auto\n");
		else
			writer.write("  am1 uhf doublet truste t=auto\n");
		
		writer.write(outputFile.getName() + "\n");
		writer.write(" Comment\n");
		
		NumberFormat formatter = new DecimalFormat("#0.000000"); 
		
		//" C             -1.214500  1    0.694000  1    0.000000  1 #   "
		for (Triplet<Double, Double, Double> carbon : carbons) {

			double x = carbon.getX();
			double y = carbon.getY();
			double z = carbon.getZ();

			String xStr = formatter.format(x).replace(",", ".");
			String yStr = formatter.format(y).replace(",", ".");
			String zStr = formatter.format(z).replace(",", ".");


			writer.write(" C\t" + xStr + "\t1\t" + yStr + "\t1\t" + zStr + "\t1 #   \n");
		}
		
		for (Triplet<Double, Double, Double> hydrogen : hydrogens) {
			double x = hydrogen.getX();
			double y = hydrogen.getY();
			double z = hydrogen.getZ();
			
			String xStr = formatter.format(x).replace(",", ".");
			String yStr = formatter.format(y).replace(",", ".");
			String zStr = formatter.format(z).replace(",", ".");
			
			writer.write(" H\t" + xStr + "\t1\t" + yStr + "\t1\t" + zStr + "\t1 #   \n");
		}
		
		writer.write(" 0\t" + "0.000000" + "\t0\t" + "0.000000" + "\t0\t" + "0.000000" + "\t0\n");
		
		writer.close();
	}
	
	public static Geometry parseAmpacGeometry(String inputFilePath) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
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
