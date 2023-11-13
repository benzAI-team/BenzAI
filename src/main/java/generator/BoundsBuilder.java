package generator;

import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import utils.HexNeighborhood;

enum BoundsBuilder {
	;

	static UndirectedGraph buildGLB2(GeneralModel model) {
		return new UndirectedGraph(model.getProblem(), model.getNbHexagonsCoronenoid(), SetType.LINKED_LIST, false);
	}
	
	static UndirectedGraph buildGUB2(GeneralModel model) {
		
		UndirectedGraph GUB = new UndirectedGraph(model.getProblem(), model.getNbHexagonsCoronenoid(), SetType.LINKED_LIST, false);
		
		int [][] matrix = model.getHexagonIndicesMatrix();
		int diameter = model.getDiameter();
		
		/*
		 * Building nodes
		 */

		for (int[] ints : matrix) {
			for (int j = 0; j < matrix.length; j++) {
				if (ints[j] != -1) {
					GUB.addNode(model.getHexagonCompactIndicesTab()[ints[j]]);
				}
			}
		} 
		
		/*
		 * Building edges
		 */
		
		int [][] edges = new int[model.getNbHexagonsCoronenoid()][model.getNbHexagonsCoronenoid()];
		
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix.length ; j++) {
				
				if (matrix[i][j] != -1) {
				
					int u = model.getHexagonCompactIndicesTab()[matrix[i][j]];
					
					int [] N = new int [6];
					
					for (int k = 0 ; k < 6 ; k++)
						N[k] = -1;
					for(HexNeighborhood neighbor : HexNeighborhood.values()){
						int x =  j + neighbor.dx();
						int y = i + neighbor.dy();
						if(x >= 0 && x <= diameter - 1 && y >= 0 && y <= diameter - 1 )
							N[neighbor.getIndex()] = matrix[y][x];
					}

//					if (i > 0)
//						N[0] = matrix[i - 1][j];
//
//					if (j + 1 < diameter)
//						N[1] = matrix[i][j + 1];
//
//					if (i + 1 < diameter && j + 1 < diameter)
//						N[2] = matrix[i + 1][j + 1];
//
//					if (i + 1 < diameter)
//						N[3] = matrix[i + 1][j];
//
//					if (j > 0)
//						N[4] = matrix[i][j - 1];
//
//					if (i > 0 && j > 0)
//						N[5] = matrix[i - 1][j - 1];
					
					for (int k = 0 ; k < 6 ; k++) {
						int v;
						
						if (N[k] == -1)
							v = -1;
						else v = model.getHexagonCompactIndicesTab()[N[k]];
						
						if (u != -1 && v != -1) {
							if (edges[u][v] == 0) {
								
								edges[u][v] = 1;
								edges[v][u] = 1;
								
								GUB.addEdge(u, v);
							}
						}
					}
				}
			}
		}
		
		return GUB;
	}

}
