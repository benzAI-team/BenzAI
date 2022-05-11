package molecules;

import utils.Triplet;

public class AtomGeometry {

	private String label;
	private Triplet<Double, Double, Double> geometry;

	public AtomGeometry(String label, double x, double y, double z) {
		this.label = label;
		geometry = new Triplet<>(x, y, z);
	}

	public String getLabel() {
		return label;
	}

	public double getX() {
		return geometry.getX();
	}

	public double getY() {
		return geometry.getY();
	}

	public double getZ() {
		return geometry.getZ();
	}

	@Override
	public String toString() {
		return label + " : " + geometry.toString();
	}

	@Override
	public boolean equals(Object o) {
		AtomGeometry a = (AtomGeometry) o;
		return a.getLabel() == getLabel() && a.getX() == getX() && a.getY() == getY() && a.getZ() == getZ();
	}
}
