package gaussbuilder;

import java.util.ArrayList;

import utils.Couple;
import utils.Triplet;

public class Geometry extends Couple<Triplet<Double, Double, Double> [], ArrayList<Triplet<Double, Double, Double>>>{

	private ArrayList<Integer> hydrogensConnections = new ArrayList<>();
	
	public Geometry(Triplet<Double, Double, Double>[] x, ArrayList<Triplet<Double, Double, Double>> y, ArrayList<Integer> hydrogensConnections) {
		super(x, y);
		this.hydrogensConnections = hydrogensConnection;
	}

	public Triplet<Double, Double, Double>[] getCarbons() {
		return x;
	}
	
	public ArrayList<Triplet<Double, Double, Double>> getHydrogens() {
		return y;
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for (Triplet<Double, Double, Double> carbon : x)
			builder.append(" C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() +" \n");
		
		for (Triplet<Double, Double, Double> carbon : y)
			builder.append(" H " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() +" \n");
		
		return builder.toString();
	}
}
