package database.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class NICS {

	private Long moleculeId;
	private String name;

	private int nbHexagons;
	private int nbCarbons;
	private int nbHydrogens;
	private double irregularity;

	private Double[] nicsValues;

	public NICS(Long moleculeId, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity) {
		this.moleculeId = moleculeId;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;

		nicsValues = new Double[nbHexagons];
	}

	public void setNICSValue(int indexHexagon, Double value) {
		nicsValues[indexHexagon] = value;
	}

	public static ArrayList<NICS> buildNics(ArrayList<NICSEntry> entries) {

		HashMap<Long, ArrayList<NICSEntry>> sortedEntries = new HashMap<>();

		for (NICSEntry entry : entries) {
			if (sortedEntries.get(entry.getId()) == null) {
				sortedEntries.put(entry.getId(), new ArrayList<>());
				sortedEntries.get(entry.getId()).add(entry);
			}

			else
				sortedEntries.get(entry.getId()).add(entry);
		}

		ArrayList<NICS> nicsResults = new ArrayList<>();
		for (int i = 0; i < sortedEntries.size(); i++)
			nicsResults.add(null);

		for (Entry<Long, ArrayList<NICSEntry>> entry : sortedEntries.entrySet()) {

			ArrayList<NICSEntry> nicsEntries = entry.getValue();

			Long moleculeId = nicsEntries.get(0).getId();
			String name = nicsEntries.get(0).getName();
			int nbHexagons = nicsEntries.get(0).getNbHexagons();
			int nbCarbons = nicsEntries.get(0).getNbCarbons();
			int nbHydrogens = nicsEntries.get(0).getNbHydrogens();
			double irregularity = nicsEntries.get(0).getIrregularity();

			NICS nics = new NICS(moleculeId, name, nbHexagons, nbCarbons, nbHydrogens, irregularity);

			for (NICSEntry nicsEntry : nicsEntries) {
				int indexHexagon = nicsEntry.getIdHexagon();
				double value = nicsEntry.getValue();
				nics.setNICSValue(indexHexagon, value);
			}

			nicsResults.add(nics);

		}

		return nicsResults;
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

	public Double[] getNicsValues() {
		return nicsValues;
	}

}
