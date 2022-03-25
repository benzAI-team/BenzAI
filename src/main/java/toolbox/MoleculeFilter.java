package toolbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import classifier.Irregularity;
import molecules.Molecule;
import parsers.GraphParser;
import utils.Couple;

public class MoleculeFilter {

	private static Couple<Integer, Integer> countCarbonsAndHydrogens(Molecule molecule) {
		
		return new Couple<Integer, Integer>(molecule.getNbNodes(), molecule.getNbHydrogens());
	}
	
	private static Irregularity computeParameterOfIrregularity(Molecule molecule) {
		
		if (molecule.getNbHexagons() == 1)
			return null;
		
		int [] N = new int [4];
		int [] checkedNodes = new int [molecule.getNbNodes()];
			
		ArrayList<Integer> V = new ArrayList<Integer>();
			
		for (int u = 0 ; u < molecule.getNbNodes() ; u++) {
			int degree = molecule.degree(u);
			if (degree == 2 && !V.contains(u)) {
				V.add(u);
				checkedNodes[u] = 0;
			}
				
			else if (degree != 2)
				checkedNodes[u] = -1;
		}
				
		ArrayList<Integer> candidats = new ArrayList<Integer>();
			
		while (true) {
				
			int firstVertice = -1;
			for (Integer u : V) {
				if (checkedNodes[u] == 0) {
					firstVertice = u;
					break;
				}
			}
				
			if (firstVertice == -1)
				break;
				
			candidats.add(firstVertice);
			checkedNodes[firstVertice] = 1;
				
			int nbNeighbors = 1;
			
			while(candidats.size() > 0) {
					
				int candidat = candidats.get(0);
					
				for (int i = 0 ; i < molecule.getNbNodes() ; i++) {
					if (molecule.getAdjacencyMatrix()[candidat][i] == 1 && checkedNodes[i] == 0) {
							
						checkedNodes[i] = 1;
						nbNeighbors ++;
						candidats.add(i);
					}
				}
					
				candidats.remove(candidats.get(0));
			}
				
			N[nbNeighbors - 1] += nbNeighbors;
		}
			
		double XI = ((double)N[2] + (double)N[3]) / ((double)N[0] + (double)N[1] + (double)N[2] + (double)N[3]);
		return new Irregularity(N, XI);
	}
	
	private static void check(Molecule molecule, String filename) {
		
		Couple<Integer, Integer> result = countCarbonsAndHydrogens(molecule); 
		
		int nbCarbons = result.getX();
		int nbHydrogens = result.getY();
		double irregularity = computeParameterOfIrregularity(molecule).getXI();
		
		if (nbCarbons >= 34 && nbCarbons <= 50 && irregularity >= 0.5)
			System.out.println(filename + " " + nbCarbons + " " + nbHydrogens + " " + irregularity);
	}
	
	private static void readFile(String filename) throws IOException{
		Molecule molecule = GraphParser.parseUndirectedGraph(filename, null, false);
		check(molecule, filename);
	}
	
	private static void readAllFiles(String filename) throws IOException {
		
		BufferedWriter log = new BufferedWriter(new FileWriter(new File("log")));
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		String line;
		
		while((line = reader.readLine()) != null) {
			log.write(line + "\n");
			readFile(line);
		}
		
		reader.close();
		log.close();
	}
	
	public static void main(String [] args) throws IOException{
		readAllFiles("C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\graph_files\\list_graph_files.txt");
	}
}
