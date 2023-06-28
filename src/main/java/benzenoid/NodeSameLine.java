package benzenoid;

public class NodeSameLine implements Comparable<NodeSameLine>{

	private final int index;
	private final int x;
	
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
		return Integer.compare(x, o.getX());
	}
}
