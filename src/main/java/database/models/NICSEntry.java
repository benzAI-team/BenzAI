package database.models;

import java.util.Map;

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

	@SuppressWarnings("rawtypes")
	public static NICSEntry buildQueryContent(Map result) {

		Long idMolecule = (Long) (result.get("id"));
		String name = (String) result.get("name");
		int nbHexagons = (int) ((double) result.get("nbHexagons"));
		int nbCarbons = (int) ((double) result.get("nbCarbons"));
		int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
		double irregularity = (double) result.get("irregularity");

		double idNics = (double) result.get("idNics");
		int idHexagon = (int) result.get("idHexagon");
		double value = (int) result.get("value");

		return new NICSEntry(idMolecule, name, nbHexagons, nbCarbons, nbHydrogens, irregularity, idHexagon, value);
	}
}
