package database;

public class IRSpectrumResult {

	private final Long id;

	private final String name;

	private final int nbHexagons;

	private final int nbCarbons;

	private final int nbHydrogens;

	private final double irregularity;

	private final Long idSpectrum;

	private final String frequencies;

	private final String intensities;

	private final String finalEnergies;

	private final double zeroPointEnergy;

	public IRSpectrumResult(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity,
			Long idSpectrum, String frequencies, String intensities, String finalEnergies, double zeroPointEnergy) {
		this.id = id;
		this.name = name;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.idSpectrum = idSpectrum;
		this.frequencies = frequencies;
		this.intensities = intensities;
		this.finalEnergies = finalEnergies;
		this.zeroPointEnergy = zeroPointEnergy;
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

	public Long getIdSpectrum() {
		return idSpectrum;
	}

	public String getFrequencies() {
		return frequencies;
	}

	public String getIntensities() {
		return intensities;
	}

	public String getFinalEnergies() {
		return finalEnergies;
	}

	public double getZeroPointEnergy() {
		return zeroPointEnergy;
	}

	@Override
	public String toString() {
		return "IRSpectrumResult [id=" + id + ", name=" + name + ", nbHexagons=" + nbHexagons + ", nbCarbons="
				+ nbCarbons + ", nbHydrogens=" + nbHydrogens + ", irregularity=" + irregularity + ", idSpectrum="
				+ idSpectrum + ", frequencies=" + frequencies + ", intensities=" + intensities + ", finalEnergies="
				+ finalEnergies + ", zeroPointEnergy=" + zeroPointEnergy + "]";
	}

}
