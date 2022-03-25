package utils;

import java.util.List;

import molecules.Node;

public class EdgeSet {

	private List<Node> firstVertices;
	private List<Node> secondVertices;
	
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
			b.append("(" + firstVertices.get(i).getIndex() + "--" + secondVertices.get(i).getIndex() + ") ");
		}
		
		return b.toString();
	}
}
