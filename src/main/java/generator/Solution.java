package generator;

import java.util.ArrayList;

import molecules.Node;


public class Solution {

	private int[] vertices;
	private int[][] coordsMatrixCoronenoid;
	private Node[] coronenoidNodes;

	public Solution(Node[] coronenoidNodes, int[][] coordsMatrixCoronenoid, int[] vertices) {
		this.coronenoidNodes = coronenoidNodes;
		this.coordsMatrixCoronenoid = coordsMatrixCoronenoid;
		this.vertices = vertices;
	}

	
	
	public int [] getVertices() {
		return vertices;
	}
	
	public int getVertex(int index) {
		return vertices[index];
	}
	
	public Node [] getCoronenoidNodes() {
		return coronenoidNodes;
	}
	
	public Node getCoronenoidNode(int index) {
		return coronenoidNodes[index];
	}
	
	public int [] rotation180() {
		
		int diameter = coordsMatrixCoronenoid.length;
		int centerIndex = coordsMatrixCoronenoid[(diameter - 1) / 2][(diameter - 1) / 2];
		Node center = getCoronenoidNode(centerIndex);
		
		int xc = center.getX();
		int yc = center.getX();
		
		int [] rotation = new int[vertices.length];
	
		for (int i = 0 ; i < vertices.length ; i++) {
			
			int vertex = getVertex(i);
			int x1 = getCoronenoidNode(vertex).getX();
			int y1 = getCoronenoidNode(vertex).getY();
			
			int xd = xc - x1;
			int yd = yc - y1;
			
			int x2 = xc + xd;
			int y2 = yc + yd;
			
			int image = coordsMatrixCoronenoid[x2][y2];
			rotation[i] = image;
		}
		
		return rotation;
	}
	
	public ArrayList<Integer[]> translationsFaceMirror() {
		
		int diameter = coordsMatrixCoronenoid.length;
		
		ArrayList<Integer[]> translations = new ArrayList<>();
		
		int [] rotation = rotation180();
		
		for (int shift = -1 * diameter ; shift <= diameter ; shift ++) {
			
			Integer[] translation1 = new Integer[vertices.length];
			Integer [] translation2 = new Integer[vertices.length];
			
			boolean embedded1 = true;
			boolean embedded2 = true;
			
			for (int i = 0 ; i < vertices.length ; i++) {
				
				int vertexIndex1 = vertices[i];
				int vertexIndex2 = rotation[i];
				
				Node n1 = coronenoidNodes[vertexIndex1];
				Node n2 = coronenoidNodes[vertexIndex2];

				int x1 = n1.getX() + shift;
				int y1 = n1.getY() + shift;
				
				int x2 = n2.getX() + shift;
				int y2 = n2.getX() + shift;
				
				if (!(x1 >= 0 && x1 < diameter && y1 >= 0 && y1 < diameter)) {
					embedded1 = false;
					break;
				}
				
				else
					translation1[i] = coordsMatrixCoronenoid[x1][y1];
				
				if (!(x2 >= 0 && x2 < diameter && y2 >= 0 && y2 < diameter)) {
					embedded2 = false;
					break;
				}
				
				else
					translation2[i] = coordsMatrixCoronenoid[x2][y2];
				
			}
			
			if (embedded1)
				translations.add(translation1);
			
			if (embedded2)
				translations.add(translation2);
		}
		
		return translations;
	}
}
