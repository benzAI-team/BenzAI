package utils;

import java.util.List;

import benzenoid.Node;

public class EdgeSet {

	private final List<Node> firstVertices;
	private final List<Node> secondVertices;
	
	public EdgeSet(List<Node> firstVertices, List<Node> secondVertices) {
		this.firstVertices = firstVertices;
		this.secondVertices = secondVertices;
	}

	public List<Node> getFirstVertices() {
		return firstVertices;
	}

	public List<Node> getSecondVertices() {
		return secondVertices;
	}
	
	public int size() {
		return firstVertices.size();
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0 ; i < firstVertices.size() ; i++) {
			b.append("(").append(firstVertices.get(i).getIndex()).append("--").append(secondVertices.get(i).getIndex()).append(") ");
		}
		
		return b.toString();
	}
}
