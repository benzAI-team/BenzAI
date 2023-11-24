package database.models;

import java.util.Map;

public class NICSEntry {

	private final Long id;
	private final String name;
	private final int nbHexagons;
	private final int nbCarbons;
	private final int nbHydrogens;
	private final double irregularity;

	private final int idHexagon;
	private final double value;

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

		Long idMolecule = (Long) (result.get("idBenzenoid"));
		String label = (String) result.get("label");
		int nbHexagons = (int) ((double) result.get("nbHexagons"));
		int nbCarbons = (int) ((double) result.get("nbCarbons"));
		int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
		double irregularity = (double) result.get("irregularity");

		double idNics = (double) result.get("idNics");
		int idHexagon = (int) result.get("idHexagon");
		double value = (int) result.get("value");

		return new NICSEntry(idMolecule, label, nbHexagons, nbCarbons, nbHydrogens, irregularity, idHexagon, value);
	}
}
