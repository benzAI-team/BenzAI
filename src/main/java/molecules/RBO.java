package molecules;

public class RBO {

	private Molecule molecule;
	private double [][] statistics;
	private double [] RBO;
	
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
