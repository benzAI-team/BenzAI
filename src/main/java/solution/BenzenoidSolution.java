package solution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class BenzenoidSolution {

	private SimpleGraph<Integer, DefaultEdge> hexagonGraph;
	private SimpleGraph<Integer, DefaultEdge> carbonGraph;
	private int nbCouronnes;
	private String name;

	private int[] hexagonsCorrespondances;

	public BenzenoidSolution(UndirectedGraph ub, int nbCouronnes, String nameBase, int[] hexagonsCorrespondances) {
		hexagonGraph = BenzenoidSolution.choco2JGraphT(ub, hexagonsCorrespondances);
		carbonGraph = GraphConversion.toCarbonGraph(hexagonGraph, 2 * nbCouronnes - 1);
		this.nbCouronnes = nbCouronnes;
		this.name = nameBase;
		this.hexagonsCorrespondances = hexagonsCorrespondances;
	}

	public BenzenoidSolution(BenzenoidSolution benzenoidSolution, int[] hexagonsCorrespondances) {
		hexagonGraph = benzenoidSolution.getHexagonGraph();
		carbonGraph = benzenoidSolution.getCarbonGraph();
		this.nbCouronnes = benzenoidSolution.getNbCouronnes();
		this.name = benzenoidSolution.getName();
		this.hexagonsCorrespondances = hexagonsCorrespondances;
	}

	public static SimpleGraph<Integer, DefaultEdge> choco2JGraphT(UndirectedGraph chocoGraph,
			int[] hexagonsCorrespondances) {

		SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		for (int vertex : chocoGraph.getNodes()) {
			graph.addVertex(hexagonsCorrespondances[vertex]);
		}

		for (int vertex : chocoGraph.getNodes()) {
			for (int succ : chocoGraph.getNeighborsOf(vertex))
				graph.addEdge(hexagonsCorrespondances[vertex], hexagonsCorrespondances[succ]);
		}

		return graph;
	}

	public void saveCML(int number, String path) {
		File fichier = new File(path + "/" + name + "-" + number + ".cml");
		try {
			FileWriter writer = new FileWriter(fichier, false);
			writer.write(GraphConversion.toCML(hexagonGraph, 2 * nbCouronnes - 1));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getNbCouronnes() {
		return nbCouronnes;
	}

	public void setNbCouronnes(int nbCouronnes) {
		this.nbCouronnes = nbCouronnes;
	}

	public String getName() {
		return name;
	}

	public void setName(String nameBase) {
		this.name = nameBase;
	}

	public void setHexagonGraph(SimpleGraph<Integer, DefaultEdge> hexagonGraph) {
		this.hexagonGraph = hexagonGraph;
	}

	public void setCarbonGraph(SimpleGraph<Integer, DefaultEdge> carbonGraph) {
		this.carbonGraph = carbonGraph;
	}

	public SimpleGraph<Integer, DefaultEdge> getHexagonGraph() {
		return hexagonGraph;
	}

	public SimpleGraph<Integer, DefaultEdge> getCarbonGraph() {
		return carbonGraph;
	}
	
	public int [] getHexagonsCorrespondances() {
		return hexagonsCorrespondances;
	}
	
	public void setHexagonsCorrespondances(int [] hexagonsCorrespondances) {
		this.hexagonsCorrespondances = hexagonsCorrespondances;
	}

}
