package parsers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import utils.Couple;
import utils.Utils;

public class GraphFileBuilder {

	private ArrayList<Integer> solution;
	private String outputFilename;
	
	private int nbCrowns;
	private int diameter;
	private int [][] coordsMatrix;
	
	public GraphFileBuilder(ArrayList<Integer> solution, String outputFilename, int nbCrowns) {
		this.solution = solution;
		this.outputFilename = outputFilename;
		this.nbCrowns = nbCrowns;
		buildCoordsMatrix();
	}
	
	private int findCandidate(int [] checkedHexagons) {
		
		for (int i = 0 ; i < checkedHexagons.length ; i++) {
			if (checkedHexagons[i] == 0)
				return i;
		}
		return -1;
	}
	
	private int [] neighborhood(int hexagon) {
		
		int [] neighborhood = new int[6];
		for (int i = 0 ; i < 6 ; i++)
			neighborhood[i] = -1;
		
		Couple<Integer, Integer> coords = Utils.getHexagonCoords(hexagon, diameter);
		int x = coords.getX();
		int y = coords.getY();
		
		//0 - HIGH-RIGHT
		if (y > 0) {
			int v = coordsMatrix[y - 1][x];
			if (v != -1)
				if (solution.get(v) == 1)
					neighborhood[0] = v;
		}
		
		//1 - RIGHT
		if (x < diameter - 1) {
			int v = coordsMatrix[y][x + 1];
			if (v != -1)
				if (solution.get(v) == 1)
					neighborhood[1] = v;
		}
		
		//2 - DOWN-RIGHT
		if (x < diameter - 1 && y < diameter - 1) {
			int v = coordsMatrix[y + 1][x + 1];
			if (v != -1)
				if (solution.get(v) == 1)
					neighborhood[2] = v;
		}
		
		//3 - DOWN-LEFT
		if (y < diameter - 1) {
			int v = coordsMatrix[y + 1][x];
			if (v != -1)
				if (solution.get(v) == 1)
					neighborhood[3] = v;
		}
		
		//4 - LEFT
		if (x > 0) {
			int v = coordsMatrix[y][x - 1];
			if (v != -1)
				if (solution.get(v) == 1)
					neighborhood[4] = v;
		}
		
		//5 - HIGH-LEFT
		if (x > 0 && y > 0) {
			int v = coordsMatrix[y - 1][x - 1];
			if (v != -1)
				if (solution.get(v) == 1)
					neighborhood[5] = v;
		}
		
		return neighborhood;
	}
	
	private boolean isValid(int [] hexagon) {
		
		for (int i = 0 ; i < hexagon.length ; i++) {
			if (hexagon[i] == -1) 
				return false;
		}
		
		return true;
	}
	
	public void buildGraphFile() throws IOException {
		int [] checkedHexagons = new int[diameter * diameter];
		for (int i = 0 ; i < checkedHexagons.length ; i++) {
			checkedHexagons[i] = -1;
		}
		
		for (int i = 0 ; i < solution.size() ; i++) {
			if (solution.get(i) == 1)
				checkedHexagons[i] = 0;
		}
		
		int n = 0;
		int indexNode = 0;
		
		int solutionSize = 0;
		for (Integer i : solution)
			if (i == 1)
				solutionSize ++;
		
		int [][] hexagons = new int [solution.size()][6];
		for (int i = 0 ; i < hexagons.length ; i++) 
			for(int j = 0 ; j < 6 ; j++)
				hexagons[i][j] = -1;
		
		
		 
		while (n < solutionSize) {
			
			int hexagon = findCandidate(checkedHexagons);
			int [] neighborhood = neighborhood(hexagon);
			
			for (int i = 0 ; i < neighborhood.length ; i++) {
				
				int neighbor = neighborhood[i];
				
				if (neighbor != -1 && checkedHexagons[neighbor] == 1) {
					
					if (i == 0) {
						hexagons[hexagon][0] = hexagons[neighbor][4];
						hexagons[hexagon][1] = hexagons[neighbor][3];			
					}
					
					else if (i == 1) {
						hexagons[hexagon][1] = hexagons[neighbor][5];
						hexagons[hexagon][2] = hexagons[neighbor][4];
					}
					
					else if (i == 2) {
						hexagons[hexagon][2] = hexagons[neighbor][0];
						hexagons[hexagon][3] = hexagons[neighbor][5];
					}
					
					else if (i == 3) {
						hexagons[hexagon][3] = hexagons[neighbor][1];
						hexagons[hexagon][4] = hexagons[neighbor][0];
					}
					
					else if (i == 4) {
						hexagons[hexagon][5] = hexagons[neighbor][1];
						hexagons[hexagon][4] = hexagons[neighbor][2];
					}
					
					else if (i == 5) {		
						hexagons[hexagon][0] = hexagons[neighbor][2];
						hexagons[hexagon][5] = hexagons[neighbor][3];
					}
				}
			}
			
			for (int i = 0 ; i < neighborhood.length ; i++) {
				if (hexagons[hexagon][i] == -1) {
					hexagons[hexagon][i] = indexNode;
					indexNode ++;
				}
			}
			
			checkedHexagons[hexagon] = 1;
			n++;
		}
		
		int [][] matrix = new int[indexNode][indexNode];
		int nbEdges = 0;
		int nbHexagons = 0;
		
		for (int hexagon = 0 ; hexagon < hexagons.length ; hexagon++) {
			if (isValid(hexagons[hexagon])) {
				nbHexagons ++;
				for (int i = 0 ; i < 6 ; i++) {
					int u = hexagons[hexagon][i];
					int v = hexagons[hexagon][(i + 1) % 6];
				
					if (matrix[u][v] == 0) {
						matrix[u][v] = 1;
						matrix[v][u] = 1;
						nbEdges ++;
					}
				}
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFilename)));
		
		writer.write("p DIMACS " + indexNode + " " + nbEdges + " " + nbHexagons + "\n");
		
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = (i+1) ; j < matrix[i].length ; j++) {
				
				if (matrix[i][j] == 1)
					writer.write("e " + i + " " + j + "\n");
			}
		}
		
		for (int hexagon = 0 ; hexagon < hexagons.length ; hexagon ++) {
			
			if (isValid(hexagons[hexagon])) {
				
				writer.write("h ");
				
				for (int i = 0 ; i < 6 ; i++) {
					writer.write(hexagons[hexagon][i] + " ");
				}
			
				writer.write("\n");
			}
		}
		writer.close();
	}
	
	private void buildCoordsMatrix() {
		
		diameter = (2 * nbCrowns) - 1;
		coordsMatrix = new int[diameter][diameter];
		
		for (int i = 0 ; i < diameter ; i++)
			for (int j = 0 ; j < diameter ; j++)
				coordsMatrix[i][j] = -1; 
		
		int index = 0;
		int m = (diameter - 1) / 2;
	
		int shift = diameter - nbCrowns;
		
		for (int i = 0 ; i < m ; i++) {
			
			for (int j = 0 ; j < diameter - shift ; j++) {
				coordsMatrix[i][j] = index; 
				index ++;
			}
			
			for (int j = diameter - shift ; j < diameter ; j++)
				index ++;
			
			shift --;
		}
		
		for (int j = 0 ; j < diameter ; j++) {
			coordsMatrix[m][j] = index;
			index ++;
		}
		
		shift = 1;
		
		for (int i = m + 1 ; i < diameter ; i++) {
			
			for (int j = 0 ; j < shift ; j++)
				index ++;
			
			for (int j = shift ; j < diameter ; j++) {
				coordsMatrix[i][j] = index;
				index ++;
			}
			
			shift ++;
		}
		
		index = 0;
	}
}
