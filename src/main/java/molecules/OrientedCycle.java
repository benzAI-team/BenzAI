package molecules;

public class OrientedCycle {

	private int size;
	private final int [] nodes;
	private final int [][] matrix;
	
	public OrientedCycle(Benzenoid molecule) {
		size = 0;
		nodes = new int [molecule.getNbNodes()];
		matrix = new int [molecule.getNbNodes()][molecule.getNbNodes()];
	}

	public int getSize() {
		return size;
	}

	public int[][] getMatrix() {
		return matrix;
	}
	
	public void addArc(int u, int v) {
		nodes[u] = 1;
		nodes[v] = 1;
		matrix[u][v] = 1;
		size ++;
	}
	
	public int matrixSize() {
		return matrix.length;
	}
	
	public boolean containsArc(int u, int v) {
		return matrix[u][v] == 1;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix[i].length ; j++) {
				
				if (matrix[i][j] == 1)
					builder.append("(").append(i).append(" -> ").append(j).append(") ");
			}
		}
		
		return builder.toString();
	}
}
