package generator;

import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import utils.Utils;
 
public class BoundsBuilder {

	public static UndirectedGraph buildGLB(GeneralModel model) {
		//return new UndirectedGraph(model.getProblem(), model.getDiameter() * model.getDiameter(), SetType.BITSET, false);
		return new UndirectedGraph(model.getProblem(), model.getDiameter() * model.getDiameter(), SetType.LINKED_LIST, false);
	}
	
	public static UndirectedGraph buildGLB2(GeneralModel model) {
		return new UndirectedGraph(model.getProblem(), model.getNbHexagonsCoronenoid(), SetType.LINKED_LIST, false);
	}
	
	public static UndirectedGraph buildGUB2(GeneralModel model) {
		
		UndirectedGraph GUB = new UndirectedGraph(model.getProblem(), model.getNbHexagonsCoronenoid(), SetType.LINKED_LIST, false);
		
		int [][] matrix = model.getCoordsMatrix();
		int diameter = model.getDiameter();
		
		/*
		 * Building nodes
		 */
		
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix.length ; j++) {
				if (matrix[i][j] != -1) {
					GUB.addNode(model.getCorrespondancesHexagons()[matrix[i][j]]);
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
				
					int u = model.getCorrespondancesHexagons()[matrix[i][j]];
					
					int [] N = new int [6];
					
					for (int k = 0 ; k < 6 ; k++)
						N[k] = -1;
					
					if (i > 0) 
						N[0] = matrix[i - 1][j];
					
					if (j + 1 < diameter)
						N[1] = matrix[i][j + 1];
					
					if (i + 1 < diameter && j + 1 < diameter)
						N[2] = matrix[i + 1][j + 1];
					
					if (i + 1 < diameter)
						N[3] = matrix[i + 1][j];
					
					if (j > 0)
						N[4] = matrix[i][j - 1];
					
					if (i > 0 && j > 0)
						N[5] = matrix[i - 1][j - 1];
					
					for (int k = 0 ; k < 6 ; k++) {
						int v;
						
						if (N[k] == -1)
							v = -1;
						else v = model.getCorrespondancesHexagons()[N[k]];
						
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
	
	public static UndirectedGraph buildGUBGeneralModel(GeneralModel model) {
		
		//UndirectedGraph GUB = new UndirectedGraph(model.getProblem(), model.getDiameter() * model.getDiameter(), SetType.BITSET, false);
		UndirectedGraph GUB = new UndirectedGraph(model.getProblem(), model.getDiameter() * model.getDiameter(), SetType.LINKED_LIST, false);
		
		int i, j;
		for(j = 0; j < model.getNbCrowns(); j++) {
			for(i = 0; i < model.getNbCrowns(); i++) {
				
				GUB.addNode(Utils.getHexagonID(i,j, model.getDiameter()));
				GUB.addNode(Utils.getHexagonID(model.getNbCrowns() - 1 + i,model.getNbCrowns() - 1 + j, model.getDiameter()));
				
				model.getCoordsMatrix()[j][i] = Utils.getHexagonID(i, j, model.getDiameter());
				model.getCoordsMatrix()[model.getNbCrowns() - 1 + j][model.getNbCrowns() - 1 + i] = Utils.getHexagonID(model.getNbCrowns() - 1 + i,model.getNbCrowns() - 1 + j, model.getDiameter());
			}
		}
		for(j = 0; j < model.getNbCrowns() - 2; j++) {
			for(i = 0; i < j + 1; i++) {
				
				GUB.addNode(Utils.getHexagonID(model.getNbCrowns() - 1 + i+1,j+1, model.getDiameter()));
				model.getCoordsMatrix()[j+1][model.getNbCrowns() - 1 + i+1] = Utils.getHexagonID(model.getNbCrowns() - 1 + i+1,j+1, model.getDiameter());
			}
		}
		for(j = 0; j < model.getNbCrowns() - 1; j++) {
			for(i = j; i < model.getNbCrowns() - 2 ; i++) {
				
				GUB.addNode(Utils.getHexagonID(i+1,model.getNbCrowns() - 1 + j+1, model.getDiameter()));
				model.getCoordsMatrix()[model.getNbCrowns() - 1 + j+1][i+1] = Utils.getHexagonID(i+1,model.getNbCrowns() - 1 + j+1, model.getDiameter());
			}
		}
		
		for(j = 0; j < model.getNbCrowns() - 1; j++) {
			for(i = 0; i < model.getNbCrowns() - 1; i++) {
				
				GUB.addEdge(Utils.getHexagonID(i,j, model.getDiameter()), Utils.getHexagonID(i+1,j, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(i,j, model.getDiameter()), Utils.getHexagonID(i,j+1, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(i,j, model.getDiameter()), Utils.getHexagonID(i+1,j+1, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(model.getNbCrowns() - 1 + i,model.getNbCrowns() - 1 + j, model.getDiameter()), Utils.getHexagonID(model.getNbCrowns() - 1 + i+1,model.getNbCrowns() - 1 + j, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(model.getNbCrowns() - 1 + i,model.getNbCrowns() - 1 + j, model.getDiameter()), Utils.getHexagonID(model.getNbCrowns() - 1 + i,model.getNbCrowns() - 1 + j+1, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(model.getNbCrowns() - 1 + i,model.getNbCrowns() - 1 + j, model.getDiameter()), Utils.getHexagonID(model.getNbCrowns() - 1 + i+1,model.getNbCrowns() - 1 + j+1, model.getDiameter()));
			
			}
		}
		for(j = 0; j < model.getNbCrowns() - 1; j++) {
			for(i = 0; i < j + 1; i++) {
				
				GUB.addEdge(Utils.getHexagonID(model.getNbCrowns() - 1 + i,j, model.getDiameter()), Utils.getHexagonID(model.getNbCrowns() - 1 + i,j + 1, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(model.getNbCrowns() - 1 + i,j, model.getDiameter()), Utils.getHexagonID(model.getNbCrowns() - 1 + i+1,j+1, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(model.getNbCrowns() - 1 + i,j+1, model.getDiameter()), Utils.getHexagonID(model.getNbCrowns() - 1 + i+1,j+1, model.getDiameter()));
			}
		}
		
		for(j = 0; j < model.getNbCrowns() - 1; j++) {
			for(i = j; i < model.getNbCrowns() - 1; i++) {
				
				GUB.addEdge(Utils.getHexagonID(i,model.getNbCrowns() - 1 + j, model.getDiameter()), Utils.getHexagonID(i+1,model.getNbCrowns() - 1 + j, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(i,model.getNbCrowns() - 1 + j, model.getDiameter()), Utils.getHexagonID(i+1,model.getNbCrowns() - 1 + j+1, model.getDiameter()));
				GUB.addEdge(Utils.getHexagonID(i+1,model.getNbCrowns() - 1 + j, model.getDiameter()), Utils.getHexagonID(i+1,model.getNbCrowns() - 1 + j+1, model.getDiameter()));
			}
		}
		
		for(j = 0; j < model.getNbCrowns() - 1; j++) {
			
			GUB.addEdge(Utils.getHexagonID(model.getDiameter() - 1,model.getNbCrowns() - 1 + j, model.getDiameter()), Utils.getHexagonID(model.getDiameter() - 1,model.getNbCrowns() + j, model.getDiameter()));
			GUB.addEdge(Utils.getHexagonID(model.getNbCrowns() - 1 + j,model.getDiameter() - 1, model.getDiameter()), Utils.getHexagonID(model.getNbCrowns() + j,model.getDiameter() - 1, model.getDiameter()));
		}
		
		return GUB;
	}
}
