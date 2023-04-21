package molecules;

public class Edge {

	private final int u;
	private final int v;
	
	public Edge(int u, int v) {
		this.u = u;
		this.v = v;
	}

	public int getU() {
		return u;
	}

	public int getV() {
		return v;
	}

	@Override
	public String toString() {
		return "(" + u + " - " + v + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		Edge edge = (Edge)o;
		return (u == edge.getU() && v == edge.getV()) || (u == edge.getV() && v == edge.getU()); 
	}
}
