package utils;

public class HexagonAromaticity implements Comparable<HexagonAromaticity>{

	private int index;
	private Double aromaticity;
	
	public HexagonAromaticity(int index, Double aromaticity) {
		super();
		this.index = index;
		this.aromaticity = aromaticity;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Double getAromaticity() {
		return aromaticity;
	}
	
	@Override
	public String toString() {
		return index + " : " + aromaticity;
	}

	@Override
	public int compareTo(HexagonAromaticity o) {
		return aromaticity.compareTo(o.getAromaticity());
	}
}
