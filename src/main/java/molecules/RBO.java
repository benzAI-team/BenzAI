package molecules;

public class RBO {

	private final Molecule molecule;
	private final double [][] statistics;
	private final double [] RBO;
	
	public RBO(Molecule molecule, double[][] statistics, double[] rBO) {
		this.molecule = molecule;
		this.statistics = statistics;
		RBO = rBO;
	}
	
	public Molecule getMolecule() {
		return molecule;
	}
	
	public double[][] getStatistics() {
		return statistics;
	}
	
	public double[] getRBO() {
		return RBO;
	}
}
