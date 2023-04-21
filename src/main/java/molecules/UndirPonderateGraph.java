package molecules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import utils.RelativeMatrix;

public class UndirPonderateGraph {
private final int nbNodes;
    private final int nbEdges;
    private final int nbHexagons;
	
	private final RelativeMatrix coords;
	
	private final int maxIndex;
	
	private final ArrayList<ArrayList<Integer>> edgeMatrix;
	private final int [][] adjacencyMatrix;
	
	private final ArrayList<String> edgesString;
	private final ArrayList<String> hexagonsString;
	
	private final Node [] nodesRefs;
	
	private final int [][] hexagons;
	
	private int [][] dualGraph;
	
	/**
	 * Constructors
	 */
	
	public UndirPonderateGraph(int nbNodes, int nbEdges, int nbHexagons, ArrayList<ArrayList<Integer>> edgeMatrix,
			int[][] adjacencyMatrix, ArrayList<String> edgesString, ArrayList<String> hexagonsString,
			Node[] nodesRefs, RelativeMatrix nodesMem, RelativeMatrix coords, int maxIndex) {

		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbHexagons = nbHexagons;
		this.edgeMatrix = edgeMatrix;
		this.adjacencyMatrix = adjacencyMatrix;
		this.edgesString = edgesString;
		this.hexagonsString = hexagonsString;
		this.nodesRefs = nodesRefs;
		this.coords = coords;
		this.maxIndex = maxIndex;
		
		hexagons = new int [nbHexagons][6];
		initHexagons();
		
		computeDualGraph();
	}

	public int [][] getDualGraph() {
		return dualGraph;
	}
	
	public void computeDualGraph() {
		
		dualGraph = new int [nbHexagons][6];
	
		for (int i = 0 ; i < nbHexagons ; i++)
			for (int j = 0 ; j < 6 ; j++)
				dualGraph[i][j] = -1;
		
		ArrayList<Integer> candidats = new ArrayList<Integer>();
		candidats.add(0);
		
		int index = 0;
		
		while (index < nbHexagons) {
		
			int candidat = candidats.get(index);
			int [] candidatHexagon = hexagons[candidat];
			
			for (int i = 0 ; i < candidatHexagon.length ; i++) {
				
				int u = candidatHexagon[i];
				int v = candidatHexagon[(i+1) % 6];
				
				System.out.print("");
				
				for (int j = 0 ; j < nbHexagons ; j++) {
					if (j != candidat) { //j != i avant
						
						int contains = 0;
						for (int k = 0 ; k < 6 ; k++) {
							if (hexagons[j][k] == u || hexagons[j][k] == v)
								contains ++;
						}
						
						if (contains == 2) {
							
							dualGraph[candidat][i] = j;
							
							if (!candidats.contains(j))
								candidats.add(j);
							
							break;
						}
					}
				}
				
			}
			index ++;
		}
	}
	
	public void initHexagons() {
		int index = 0;
		for (String hexagon : hexagonsString) {
			String [] sHexagon = hexagon.split(" ");
			for (int i = 1 ; i < sHexagon.length ; i++) {
				String []  sNodeStr = sHexagon[i].split(Pattern.quote("_"));
				int x = Integer.parseInt(sNodeStr[0]);
				int y = Integer.parseInt(sNodeStr[1]);
				hexagons[index][i-1] = coords.get(x, y);
			}
			index ++;
		}
	}

	/**
	 * Getters and setters
	 */
	
	public int getNbNodes() {
		return nbNodes;
	}

	public int getNbEdges() {
		return nbEdges;
	}


	public int getNbHexagons() {
		return nbHexagons;
	}


	public ArrayList<ArrayList<Integer>> getEdgeMatrix() {
		return edgeMatrix;
	}

	public int[][] getAdjacencyMatrix(){
		return adjacencyMatrix;
	}
	
	public ArrayList<String> getEdgesString() {
		return edgesString;
	}

	public ArrayList<String> getHexagonsString() {
		return hexagonsString;
	}
	
	public Node getNodeRef(int index) {
		return nodesRefs[index];
	}
	
	public Node [] getNodeRefs() {
		return nodesRefs;
	}
	
	public int [][] getHexagons() {
		return hexagons;
	}
	
	/**
	 * Class's methods
	 */
	
	public int getMaxIndex() {
		return maxIndex;
	}
}
