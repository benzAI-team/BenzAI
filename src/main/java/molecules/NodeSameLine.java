package molecules;

public class NodeSameLine implements Comparable<NodeSameLine>{

	private int index;
	private int x;
	
	public NodeSameLine(int index, int x) {
		this.index = index;
		this.x = x;
	}

	public int getIndex() {
		return index;
	}
	
	public int getX() {
		return x;
	}
	
	@Override
	public int compareTo(NodeSameLine o) {
		if (x < o.getX())
			return -1;
		
		else if (x == o.getX())
			return 0;
		
		else
			return 1;
	}
}
