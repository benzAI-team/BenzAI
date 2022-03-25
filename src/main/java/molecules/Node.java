package molecules;

public class Node implements Comparable<Node>{

	private int x, y, index;
	
	public Node(int x, int y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
	}

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public boolean equals(Object o) {
		Node node = (Node)o;
		return (x == node.getX() && y == node.getY() && index == node.getIndex());
	}
	
	public String getDimacsStr() {
		return x + "_" + y;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ") : " + index;
	}

	@Override
	public int compareTo(Node o) {
		
		if (y < o.getY())
			return -1;
		
		else if (y > o.getY())
			return 1;
		
		else {
			
			if (x < o.getX())
				return -1;
			
			else if (x > o.getX())
				return 1;
			
			else
				return 0;
		}
			
	}
	
	
}
