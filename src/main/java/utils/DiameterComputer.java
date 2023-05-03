package utils;

public enum DiameterComputer {
    ;

    static final int INF = 99999, V = 4;

	public static int[][] floydWarshall(int[][] graph) {
		int[][] dist = new int[V][V];

		for (int i = 0; i < V; i++)
            System.arraycopy(graph[i], 0, dist[i], 0, V);

		for (int k = 0; k < V; k++) {
			// Pick all vertices as source one by one
			for (int i = 0; i < V; i++) {
				for (int j = 0; j < V; j++) {
					if (dist[i][k] + dist[k][j] < dist[i][j])
						dist[i][j] = dist[i][k] + dist[k][j];
				}
			}
		}

		for (int i = 0; i < dist.length; i++)
			for (int j = 0; j < dist[i].length; j++)
				if (dist[i][j] == INF)
					dist[i][j] = -1;

		return dist;
	}

	// Driver program to test above function
	public static void main(String[] args) {
		/*
		 * Let us create the following weighted graph 10 (0)------->(3) | /|\ 5 | | | |
		 * 1 \|/ | (1)------->(2) 3
		 */
		int[][] graph = { { 0, 5, INF, 10 }, { INF, 0, 3, INF }, { INF, INF, 0, 1 }, { INF, INF, INF, 0 } };
		int[][] dist = floydWarshall(graph);
		System.out.print("");
	}
}
