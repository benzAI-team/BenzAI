package database.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import molecules.AtomGeometry;

public class IMS2D1A {

	private Long moleculeId;
	private String name;
	private int nbHexagons;
	private int nbCarbons;
	private int nbHydrogens;
	private double irregularity;

	private ArrayList<AtomGeometry> geometry;

	public IMS2D1A(Long moleculeId, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity) {

		this.moleculeId = moleculeId;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		geometry = new ArrayList<>();
	}

	public void setGeometry(ArrayList<AtomGeometry> geometry) {
		this.geometry = geometry;
	}

	public static ArrayList<IMS2D1A> buildIMS2D1A(ArrayList<IMS2D1AEntry> entries) {

		HashMap<Long, ArrayList<IMS2D1AEntry>> sortedEntries = new HashMap<>();

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
		}

		return null;
	}
}
