package benzenoid;

public class RBO {

	private final Benzenoid molecule;
	private final double [][] statistics;
	private final double [] RBO;
	
	public RBO(Benzenoid molecule, double[][] statistics, double[] rBO) {
		this.molecule = molecule;
		this.statistics = statistics;
		RBO = rBO;
	}
	
	public Benzenoid getMolecule() {
		return molecule;
	}
	
	public double[][] getStatistics() {
		return statistics;
	}
	
	public double[] getRBO() {
		return RBO;
	}
}
