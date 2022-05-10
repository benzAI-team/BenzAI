package database.models;

public class NICSEntry {

	private Long id;
	private String name;
	private int nbHexagons;
	private int nbCarbons;
	private int nbHydrogens;
	private double irregularity;
	
	private int idHexagon;
	private double value;
	
	public NICSEntry(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity,
			int idHexagon, double value) {
		super();
		this.id = id;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.idHexagon = idHexagon;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getNbHexagons() {
		return nbHexagons;
	}

	public int getNbCarbons() {
		return nbCarbons;
	}

	public int getNbHydrogens() {
		return nbHydrogens;
	}

	public double getIrregularity() {
		return irregularity;
	}

	public int getIdHexagon() {
		return idHexagon;
	}

	public double getValue() {
		return value;
	}
}
