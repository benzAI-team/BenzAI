package database.models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import molecules.AtomGeometry;

public class IMS2D1A {

	private final Long moleculeId;
	private final String name;
	private final int nbHexagons;
	private final int nbCarbons;
	private final int nbHydrogens;
	private final double irregularity;

	private ArrayList<AtomGeometry> geometry;
	private ArrayList<IMS2D1APoint> points;

	public IMS2D1A(Long moleculeId, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity) {

		this.moleculeId = moleculeId;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
	}

	public IMS2D1A(Long moleculeId, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity, ArrayList<AtomGeometry> geometry, ArrayList<IMS2D1APoint> points) {

		this.moleculeId = moleculeId;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.geometry = geometry;
		this.points = points;
	}
	
	public void setGeometry(ArrayList<AtomGeometry> geometry) {
		this.geometry = geometry;
	}

	public void setPoints(ArrayList<IMS2D1APoint> points) {
		this.points = points;
	}
	
	public static ArrayList<IMS2D1A> buildIMS2D1A(ArrayList<IMS2D1AEntry> entries) {

		HashMap<Long, ArrayList<IMS2D1AEntry>> sortedEntries = new HashMap<>();
		ArrayList<IMS2D1A> ims2d1as = new ArrayList<>();
		
		for (IMS2D1AEntry entry : entries) {
			if (sortedEntries.get(entry.getId()) == null) {
				sortedEntries.put(entry.getId(), new ArrayList<>());
				sortedEntries.get(entry.getId()).add(entry);
			}

			else
				sortedEntries.get(entry.getId()).add(entry);
		}

		for (Entry<Long, ArrayList<IMS2D1AEntry>> entry : sortedEntries.entrySet()) {

			ArrayList<IMS2D1AEntry> ims2d1aEntries = entry.getValue();

			Long moleculeId = ims2d1aEntries.get(0).getId();
			String name = ims2d1aEntries.get(0).getName();
			int nbHexagons = ims2d1aEntries.get(0).getNbHexagons();
			int nbCarbons = ims2d1aEntries.get(0).getNbCarbons();
			int nbHydrogens = ims2d1aEntries.get(0).getNbHydrogens();
			double irregularity = ims2d1aEntries.get(0).getIrregularity();

			IMS2D1A ims2d1a = new IMS2D1A(moleculeId, name, nbHexagons, nbCarbons, nbHydrogens, irregularity);

			/*
			 * Retrieving geometry
			 */
			
			ArrayList<AtomGeometry> geometry = new ArrayList<>();

			for (IMS2D1AEntry ims2d1aEntry : ims2d1aEntries) {

				double x = ims2d1aEntry.getX();
				double y = ims2d1aEntry.getY();
				double z = ims2d1aEntry.getZ();
				String label = ims2d1aEntry.getLabel();

				AtomGeometry atom = new AtomGeometry(label, x, y, z);
				if (!geometry.contains(atom))
					geometry.add(atom);
			}

			ims2d1a.setGeometry(geometry);
			
			/*
			 * Retrieving point values
			 */
			
			ArrayList<IMS2D1APoint> points = new ArrayList<>();
			
			String xVectorStr = ims2d1aEntries.get(0).getxVector();
			String yVectorStr = ims2d1aEntries.get(0).getyVector();
			
			String [] split1 = xVectorStr.split(" ");
			String [] split2 = yVectorStr.split(" ");
			
			double [] xVector = new double[3];
			double [] yVector = new double[3];
			
			int nbPointsX = ims2d1aEntries.get(0).getNbPointsX();
			
			for (int i = 0 ; i < 3 ; i ++) {
				xVector[i] = Double.parseDouble(split1[i]);
				yVector[i] = Double.parseDouble(split2[i]);
			}
			
			double xNorm = Math.sqrt(xVector[0] * xVector[0] + xVector[1] * xVector[1] + xVector[2] * xVector[2]);
			double yNorm = Math.sqrt(yVector[0] * yVector[0] + yVector[1] * yVector[1] + yVector[2] * yVector[2]);
			
			for (IMS2D1AEntry ims2d1aEntry : ims2d1aEntries) {
				
				Long idPoint = ims2d1aEntry.getIdPoint();
				double value = ims2d1aEntry.getValue();
				
				int y = 0;
				
				while ((long) y * nbPointsX < idPoint)
					y++;
				y--;
				
				int x = (int) (idPoint - (y * nbPointsX));
				
				double xCoord = x * xNorm;
				double yCoord = y * yNorm;
				
				IMS2D1APoint point = new IMS2D1APoint(xCoord, yCoord, value);
				
				if (!points.contains(point))
					points.add(point);
			}
			
			ims2d1a.setPoints(points);
			ims2d1as.add(ims2d1a);
		}

		return ims2d1as;
	}

	public Long getMoleculeId() {
		return moleculeId;
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

	public ArrayList<AtomGeometry> getGeometry() {
		return geometry;
	}

	public ArrayList<IMS2D1APoint> getPoints() {
		return points;
	}
	
	public static IMS2D1A parseIMS2D1A(File ims2d1aFile) {
		
		return null;
	}
}
