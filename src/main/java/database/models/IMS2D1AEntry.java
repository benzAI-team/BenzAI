
package database.models;

public class IMS2D1AEntry {

	// benzenoid
	private Long id;
	private String name;
	private int nbHexagons;
	private int nbCarbons;
	private int nbHydrogens;
	private double irregularity;

	// ims2d1a
	private Long idIMS2D1A;
	private String xVector;
	private String yVector;
	private int nbPointsX;
	private int nbPointsY;

	// point_ims2d1a
	private Long idPoint;
	private int numPoint;
	private double value;

	public IMS2D1AEntry(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity,
			Long idIMS2D1A, String xVector, String yVector, int nbPointsX, int nbPointsY, Long idPoint, int numPoint,
			double value) {
		super();
		this.id = id;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.idIMS2D1A = idIMS2D1A;
		this.xVector = xVector;
		this.yVector = yVector;
		this.nbPointsX = nbPointsX;
		this.nbPointsY = nbPointsY;
		this.idPoint = idPoint;
		this.numPoint = numPoint;
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

	public Long getIdIMS2D1A() {
		return idIMS2D1A;
	}

	public String getxVector() {
		return xVector;
	}

	public String getyVector() {
		return yVector;
	}

	public int getNbPointsX() {
		return nbPointsX;
	}

	public int getNbPointsY() {
		return nbPointsY;
	}

	public Long getIdPoint() {
		return idPoint;
	}

	public int getNumPoint() {
		return numPoint;
	}

	public double getValue() {
		return value;
	}

}
