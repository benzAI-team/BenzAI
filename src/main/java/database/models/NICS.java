package database.models;

import java.util.ArrayList;

public class NICS {

	private Long moleculeId;
	private String name;
	
	private int nbHexagons;
	private int nbCarbons;
	private int nbHydrogens;
	private double irregularity;
	
	private Double [] nicsValues;
	
	public NICS(Long moleculeId, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity) {
		this.moleculeId = moleculeId;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		
		nicsValues = new Double [nbHexagons];
	}
	
	public void setNICSValue(int indexHexagon, Double value) {
		nicsValues[indexHexagon] = value;
	}
	
	public static ArrayList<NICS> buildNics() {
		return null;
	}
}
