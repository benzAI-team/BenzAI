package correlations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import molecules.Molecule;
import parsers.GraphParser;

public enum Parser {
    ;

    private static final String instancesPath = "/home/adrien/Documents/comparaisons_constraints/instances/";
	private static final String resultsPath = "/home/adrien/Documents/comparaisons_constraints/results/";
	
	private static void corelations(String instance) throws IOException{

		File directoryRuizMorales = new File(resultsPath + instance + "/");
		File [] files = directoryRuizMorales.listFiles();

		assert files != null;
		for (File linDatFile : files) {
			
			if (linDatFile.getName().endsWith("_lin.dat")) {
				
				String nicsDatFilename = linDatFile.getAbsolutePath().replaceAll("_lin.dat", "_nics.dat");
				File nicsDatFile = new File(nicsDatFilename);
				
				if (nicsDatFile.exists()) {
				
					String moleculeFilename = instancesPath + "/" + instance + "/" + linDatFile.getName();
					moleculeFilename = moleculeFilename.replaceAll("_lin.dat", ".graph_coord");
					File moleculeFile = new File(moleculeFilename);
				
					String moleculeName = moleculeFile.getName();
					moleculeName = moleculeName.replaceAll(".graph_coord", "");
				
					Molecule molecule = GraphParser.parseUndirectedGraph(moleculeFile);
				
					//computing classes of symmetries
					HashMap<Double, ArrayList<Integer>> symmetries = new HashMap<>();
				
					BufferedReader r = new BufferedReader(new FileReader(linDatFile));
					String line;
				
					Double [] linValues = new Double [molecule.getNbHexagons()];
					Double [] nicsValues = new Double[molecule.getNbHexagons()];
				
					while((line = r.readLine()) != null) {
					
						String [] splittedLine = line.split(" ");
						int hexagon = Integer.parseInt(splittedLine[0]);
						double value = Double.parseDouble(splittedLine[1]);
					
						linValues[hexagon] = value;
					
						if (symmetries.get(value) == null) {
							symmetries.put(value, new ArrayList<>());
							symmetries.get(value).add(hexagon);
						}
					
						else {
							symmetries.get(value).add(hexagon);
						}
					}
				
					r.close();
				
				
					r = new BufferedReader(new FileReader(nicsDatFile));

					while((line = r.readLine()) != null) {

						String [] splittedLine = line.split(" ");
						int hexagon = Integer.parseInt(splittedLine[0]);
						double value = Double.parseDouble(splittedLine[1]);
					
						nicsValues[hexagon] = value;
					}
				
				
					r.close();
				
					//Building lists
					ArrayList<Double> linList = new ArrayList<>();
					ArrayList<Double> nicsList = new ArrayList<>();
				
					for (Entry<Double, ArrayList<Integer>> entry : symmetries.entrySet()) {
				    	Double linValue = entry.getKey();
				    	ArrayList<Integer> value = entry.getValue();

						int hexagon = value.get(0);
				    	Double nicsValue = nicsValues[hexagon];
				    
				    	linList.add(linValue);
				    	nicsList.add(nicsValue);
					}
				
					Double correlationCoeff = CorrelationCoefficient.correlationCoefficient(linList, nicsList, linList.size());
					System.out.println(moleculeName);
					System.out.println(correlationCoeff + "\n");
				
				}
				
			}
		}
	}
	
	public static void linLogToDat(File moleculeFile, File logFile) throws IOException {
		
		Molecule molecule = GraphParser.parseUndirectedGraph(moleculeFile);
		double nbKekuleStructures = molecule.getNbKekuleStructures();
		double [] RI = new double[] {0.869, 0.246, 0.100, 0.041};
		
		BufferedReader r = new BufferedReader(new FileReader(logFile));
		String line;
		boolean in = false;
		int index = 0;
		
		while((line = r.readLine()) != null) {
			
			if (!in && line.startsWith("LOCAL ENERGY")) 
				in = true;
			
			else if (in && !"".equals(line)) {
				String [] circuitsStr = line.split(Pattern.quote(" : "))[1].split(" ");
				double sum = 0.0;
				for (int i = 0 ; i < 4 ; i++) {
					
					double circuit = Double.parseDouble(circuitsStr[i]);
					double ri = RI[i];
					sum += ri * circuit;
				}
				double energy = sum / nbKekuleStructures;
				System.out.println(index + " " + energy);
				index ++;
			}
			
			else if (in && "".equals(line)) {
				in = false;
			}
		}
		r.close();
	}

	public static void parseNICSResult(File file, File directory) throws IOException {
		
		int state = 0;
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;
		
		String name = null;
		String hexagons = null;
		String values = null;
		
		while((line = r.readLine()) != null) {
			
			if (state == 0) {
				name = line;
				state ++;
			}
			
			else if (state == 1) {
				values = line;
				state ++;
			}
			
			else if (state == 2) {
				hexagons = line;
				state ++;
			}
			
			else if (state == 3) {
				
				String [] splittedHexagons = hexagons.split(Pattern.quote(" > "));
				String [] splittedValues = values.split(Pattern.quote(" > "));
				
				double [] energies = new double[splittedHexagons.length];
				
				for (int i = 0 ; i < splittedHexagons.length ; i++) {
					int hexagon = Integer.parseInt(splittedHexagons[i]);
					double value = -1.0 * Double.parseDouble(splittedValues[i]);
					energies[hexagon] = value;
				}
				
				File datFile = new File(directory.getAbsolutePath() + "/" + name + "_nics.dat");
				BufferedWriter w = new BufferedWriter(new FileWriter(datFile));
				
				for (int i = 0 ; i < energies.length ; i++) {
					w.write(i + " " + energies[i] + "\n");
				}
				
				w.close();
				
				state = 0;
			}
		}
		
		r.close();
	}
	
	public static void linLinFan() throws IOException {
		
		double [] RI = new double[4];
		
		for (int i = 0 ; i < 4 ; i++) {
			
			double d = i+1;
			RI[i] = 1.0 / (d * d);
		}
		
		File directory = new File("/home/adrien/Documents/comparaisons_constraints/instances/ruiz_morales");
	
		File [] files = directory.listFiles();
		
		for (File molFile : files) {
			
			if (molFile.getName().endsWith(".graph_coord")) {
				
				String base = molFile.getAbsolutePath().replace(".graph_coord", "");
				
				File linFile = new File(base + "_0.lin");
				File linFanFile = new File(base + ".lin_fan");
				
				ArrayList<Double> linDat = new ArrayList<>();
				ArrayList<Double> linFanDat = new ArrayList<>();
				
				Molecule molecule = GraphParser.parseUndirectedGraph(molFile);
				
				BufferedReader r = new BufferedReader(new FileReader(linFile));
				String line;
				boolean in = false;
				
				while((line = r.readLine()) != null) {
					if (!in && line.startsWith("LOCAL ENERGY")) {
						in = true;
						
						
					}
					
					else if (in && !"".equals(line)) {
						String [] strCircuits = line.split(Pattern.quote(" : "))[1].split(" ");
						double sum = 0.0;
						for (int j = 0 ; j < strCircuits.length ; j++) {
							sum += Double.parseDouble(strCircuits[j]) * RI[j];
						}
						sum = sum / molecule.getNbKekuleStructures();
						
						linDat.add(sum);
					}
					
					else if (in && "".equals(line)) {
						in = false;
					}
				}
				
				r.close();
				
				r = new BufferedReader(new FileReader(linFanFile));
				in = false;

				while((line = r.readLine()) != null) {
					
					if (!in && line.startsWith("NORMALIZED")) {
						in = true;
					}
					
					else if (in && !line.startsWith("time")) {
						double v = Double.parseDouble(line.split(Pattern.quote(" : "))[1]);
						linFanDat.add(v);
					}
					
					else if (in && line.startsWith("time"))
						in = false;
				}
				
				r.close();
				
				if (linDat.size() == linFanDat.size() && linDat.size() != 0) {
					double correl = CorrelationCoefficient.correlationCoefficient(linDat, linFanDat, linDat.size());
					System.out.println(molFile.getName().replace(".graph_coord", ""));
					System.out.println(correl);
				}
			}
		}
	}
	
	public static void main(String [] args) throws IOException {
		parseNICSResult(new File("/home/adrien/Documents/comparaisons_constraints/results_finaux/resultats_normalises/chisom/results_quantum_chemistry.txt"), 
				new File("/home/adrien/Documents/comparaisons_constraints/results_finaux/resultats_normalises/chisom/"));
	}
}
