
package database.models;

import java.util.Map;

public class IMS2D1AEntry {

	// benzenoid
	private Long id;
	private String name;
	private int nbHexagons;
	private int nbCarbons;
	private int nbHydrogens;
	private double irregularity;

	// coords atoms
	private Long idAtom;
	private double x;
	private double y;
	private double z;
	private String label;

	// ims2d1a
	private Long idIMS2D1A;
	private String xVector;
	private String yVector;
	private int nbPointsX;
	private int nbPointsY;
	private double originX;
	private double originY;
	private double originZ;

	// point_ims2d1a
	private Long idPoint;
	private int numPoint;
	private double value;

	public IMS2D1AEntry(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity,
			Long idAtom, double x, double y, double z, String label, Long idIMS2D1A, String xVector, String yVector,
			int nbPointsX, int nbPointsY, double originX, double originY, double originZ, Long idPoint, int numPoint,
			double value) {

		this.id = id;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.idAtom = idAtom;
		this.x = x;
		this.y = y;
		this.z = z;
		this.label = label;
		this.idIMS2D1A = idIMS2D1A;
		this.xVector = xVector;
		this.yVector = yVector;
		this.nbPointsX = nbPointsX;
		this.nbPointsY = nbPointsY;
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
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

	public Long getIdAtom() {
		return idAtom;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public String getLabel() {
		return label;
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

	public double getOriginX() {
		return originX;
	}

	public double getOriginY() {
		return originY;
	}

	public double getOriginZ() {
		return originZ;
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

	@SuppressWarnings("rawtypes")
	public static IMS2D1AEntry buildQueryContent(Map result) {

		// benzenoid
		Long idMolecule = (Long) (result.get("id"));
		String name = (String) result.get("name");
		int nbHexagons = (int) ((double) result.get("nbHexagons"));
		int nbCarbons = (int) ((double) result.get("nbCarbons"));
		int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
		double irregularity = (double) result.get("irregularity");

		// coords atoms
		Long idAtom = (Long) (result.get("idAtom"));
		double x = (double) result.get("x");
		double y = (double) result.get("y");
		double z = (double) result.get("z");
		String label = (String) result.get("label");

		// ims2d1a
		Long idIMS2D1A = (Long) result.get("idIms2d1a");
		String xVector = (String) result.get("vectorX");
		String yVector = (String) result.get("vectorY");
		int nbPointsX = (int) result.get("nbPointsX");
		int nbPointsY = (int) result.get("nbPointsY");
		double originX = (double) result.get("originX");
		double originY = (double) result.get("originY");
		double originZ = (double) result.get("originZ");

		// point_ims2d1a
		Long idPoint = (Long) result.get("idPoint");
		int numPoint = (int) result.get("numPoint");
		double value = (double) result.get("value");

		return new IMS2D1AEntry(idMolecule, name, nbHexagons, nbCarbons, nbHydrogens, irregularity, idAtom, x, y, z,
				label, idIMS2D1A, xVector, yVector, nbPointsX, nbPointsY, originX, originY, originZ, idPoint, numPoint, value);
	}
}
