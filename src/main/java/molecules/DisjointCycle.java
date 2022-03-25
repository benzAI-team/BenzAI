package molecules;

public class DisjointCycle {

	private int [][] matrix;
	private int vertexPart1;
	private int vertexPart2;
	
	public DisjointCycle(int [][] matrix, int vertexPart1, int vertexPart2) {
		this.matrix = matrix;
		this.vertexPart1 = vertexPart1;
		this.vertexPart2 = vertexPart2;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int getVertexPart1() {
		return vertexPart1;
	}

	public int getVertexPart2() {
		return vertexPart2;
	}
	
	
}
