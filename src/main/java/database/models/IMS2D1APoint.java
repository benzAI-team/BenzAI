package database.models;

public class IMS2D1APoint {

	private double x;
	private double y;
	private double value;
	
	public IMS2D1APoint(double x, double y, double value) {

		this.x = x;
		this.y = y;
		this.value = value;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "IMS2D1APoint [x=" + x + ", y=" + y + ", value=" + value + "]";
	}

	@Override
	public boolean equals(Object o) {
		IMS2D1APoint p = (IMS2D1APoint) o;
		return p.getX() == getX() && p.getY() == getY() && p.getValue() == getValue();
	}
	
}
