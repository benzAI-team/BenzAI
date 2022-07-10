package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import classifier.Irregularity;
import molecules.Molecule;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Triplet;
import utils.Utils;

public class InsertScriptFinal {

	private static int idBenzenoid;
	private static int idName;
	private static int idSpectra;
	private static int idIMS2D1A;
	
	
	private static BufferedWriter out;
	
	public static String quote(String str) {
		return "\"" + str + "\"";
	}
	
	public static String getGeometry(File comFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(comFile));
		String line;
		StringBuilder geometry = new StringBuilder();
		boolean geo = false;
		
		while((line = reader.readLine()) != null) {
			if (line.startsWith("0 1") || line.startsWith("0 2"))
				geo = true;
			
			else {	
				if (geo && line.equals(""))
					geo = false;
			
				else if (geo) {
					String [] split = Utils.splitBySeparators(line);
					geometry.append(split[1] + "_" + split[2] + "_" + split[3] + "_" + split[4] + " ");
				}
			}
		}
		
		reader.close();
		return geometry.toString();
	}
	
	public static String fileToString(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		
		while((line = reader.readLine()) != null) {
			builder.append(line + "\n");
		}
		reader.close();
		return builder.toString();
	}
	
	public static String nicsFileToString(File nicsFile) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(nicsFile));
		String line;
		
		while((line = reader.readLine()) != null) {
			builder.append(line + " ");
		}
		
		reader.close();
		return builder.toString();
	}
	
	public static void insertIMS2D1A(File ims2d1aFile, File mapFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(ims2d1aFile));
		String line;
		
		while((line = reader.readLine())!= null)
			if (line.equals(""))
				break;
		
		line = reader.readLine();
		String [] splitOrigin = Utils.splitBySeparators(line);
		Triplet<Double, Double, Double> origin = new Triplet<>(Double.parseDouble(splitOrigin[1]), Double.parseDouble(splitOrigin[2]), Double.parseDouble(splitOrigin[3]));
		String originStr = origin.getX() + " " + origin.getY() + " " + origin.getZ();
		
		line = reader.readLine();
		line = reader.readLine();

		String [] splitv1 = Utils.splitBySeparators(line);
		Triplet<Double, Double, Double> v1 = new Triplet<>(Double.parseDouble(splitv1[1]), Double.parseDouble(splitv1[2]), Double.parseDouble(splitv1[3]));
		
		line = reader.readLine();

		String [] splitv2 = Utils.splitBySeparators(line);
		Triplet<Double, Double, Double> v2 = new Triplet<>(Double.parseDouble(splitv2[1]), Double.parseDouble(splitv2[2]), Double.parseDouble(splitv2[3]));
		
		String v1Str = v1.getX() + " " + v1.getY() + " " + v1.getZ();
		String v2Str = v2.getX() + " " + v2.getY() + " " + v2.getZ();
		
		line = reader.readLine();
		ArrayList<ArrayList<Double>> points = new ArrayList<>();
		ArrayList<Double> pointsLine = new ArrayList<>();
		
		int nbPointsX = 0;
		int nbPointsY = 0;
		boolean first = true;
		
		while((line = reader.readLine()) != null) {
			
			if (line.equals("")) {
				first = false;
				points.add(pointsLine);
				pointsLine = new ArrayList<>();
				nbPointsY ++;
			}
			
			else {
				if (first)
					nbPointsX ++;
				pointsLine.add(Double.parseDouble(line));
			}
			
		}
		
		StringBuilder pointsStr = new StringBuilder();
		for (ArrayList<Double> row : points) {
			for (Double d : row) {
				pointsStr.append(d + " ");
			}
		}
		
		reader.close();
		
		String picture = PictureConverter.pngToString(mapFile.getAbsolutePath());
		
		StringBuilder insert = new StringBuilder();
		
		insert.append("INSERT INTO ims2d_1a (idIms2d1a, idBenzenoid, vectorX, vectorY, nbPointsX, nbPointsY, origin, points, picture) VALUES (\n");
		insert.append(idIMS2D1A + ", " + idBenzenoid + ", " + quote(v1Str) + ", " + quote(v2Str) + ", " + nbPointsX + ", " + nbPointsY + ", " + quote(originStr) + ", " + 
		quote(pointsStr.toString()) + ", " + quote(picture));
		insert.append(");");
		
		out.write(insert.toString());
		
		idIMS2D1A ++;
	}
	
	// Fill the tables `benzenoid` and `name`
	public static void insertBenzenoid(File molFile) throws IOException {
		
		Molecule molecule = GraphParser.parseUndirectedGraph(molFile);
		
		
		BigDecimal irregBD;
		Irregularity irreg = Utils.computeParameterOfIrregularity(molecule);
		
		if (irreg == null)
			irregBD = new BigDecimal(-1.0).setScale(3, RoundingMode.HALF_UP);
		else
			irregBD = new BigDecimal(irreg.getXI()).setScale(3, RoundingMode.HALF_UP);
		
		int nbHexagons = molecule.getNbHexagons();
		int nbCarbons = molecule.getNbNodes();
		int nbHydrogens = molecule.getNbHydrogens();
		String inchie = "unknown";
		double irregularity = irregBD.doubleValue();
		String graphFileContent = fileToString(molFile);
		String nics = "";
		
		File nicsFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".nics"));
		if (nicsFile.exists())
			nics = nicsFileToString(nicsFile);
		
		File comFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".com"));
		
		String geometry = getGeometry(comFile);
		
		ArrayList<String> names = molecule.getNames();
		
		StringBuilder insertBenzenoid = new StringBuilder();
		insertBenzenoid.append("INSERT INTO benzenoid (idBenzenoid, nbHexagons, nbCarbons, nbHydrogens, inchie, irregularity, graphFile, nics, geometry) VALUES (\n");
		insertBenzenoid.append(idBenzenoid + ", " + nbHexagons + ", " + nbCarbons + ", " + nbHydrogens + ", " + quote(inchie) + ", " + irregularity + ", ");
		insertBenzenoid.append(quote(graphFileContent) + ", " + quote(nics) + ", " + quote(geometry) + "\n);");
		
		StringBuilder insertNames = new StringBuilder();
		
		for (String name : names) {
			insertNames.append("INSERT INTO name (idName, idBenzenoid, name) VALUES (" + idName + ", " + idBenzenoid + ", " + quote(name) + ");\n");
			idName ++;
		}
		
		out.write(insertBenzenoid.toString() + "\n");
		out.write(insertNames.toString() + "\n");
	}
	
	//fill ir_spectra table
	public static void insertIRSpectra(File molFile) throws IOException {
		File logFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".log"));
		
		ResultLogFile log = SpectrumsComputer.parseLogFile(logFile.getAbsolutePath());
		
		StringBuilder frequencies = new StringBuilder();
		for (Double frequency : log.getFrequencies())
			frequencies.append(frequency + " ");
		
		StringBuilder intensities = new StringBuilder();
		for (Double intensity : log.getIntensities())
			intensities.append(intensity + " ");
		
		double zpe = log.getZeroPointEnergy();
		
		double finalEnergy = log.getFinalEnergy().get(log.getFinalEnergy().size() - 1);
		
		StringBuilder insert = new StringBuilder();
		insert.append("INSERT INTO ir_spectra (idSpectra, idBenzenoid, frequencies, intensities, zeroPointEnergy, finalEnergy) VALUES (\n");
		
		insert.append(idSpectra + ", " + idBenzenoid + ", " + quote(frequencies.toString()) + ", " + quote(intensities.toString()) + ", " + zpe + ", " + finalEnergy);
		
		insert.append(");\n");
		
		out.write(insert.toString() + "\n");
		
		idSpectra ++;
	}
	
	public static void main(String [] args) throws IOException {
		
		File dir = new File("/home/adrien/Documents/old_log_files");
		out = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/new_insert.sql")));
		File [] files = dir.listFiles();
		
		idBenzenoid = 0;
		idName = 0;
		idIMS2D1A = 0;
		
		boolean first = true;
		
		for (File molFile : files) {
			if (molFile.getName().endsWith(".graph_coord")) {
				if (first) {
				System.out.println("Treating " + molFile.getName());
				
				insertBenzenoid(molFile);
				insertIRSpectra(molFile);
				
				File ims2dTextFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_ims2d1a.txt"));
				File ims2dMapFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_ims2d1a.png"));
				
				if (ims2dTextFile.exists() && ims2dMapFile.exists())
					insertIMS2D1A(ims2dTextFile, ims2dMapFile);
				
				idBenzenoid ++;
				}
				
				//first = false;
			}
		}
		
		out.close();
	}

	
}
