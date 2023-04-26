package solution;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import molecules.Molecule;

public class GraphConversion {

	public static SimpleGraph<Integer, DefaultEdge> buildCarbonGraph(Molecule molecule) {
		
		SimpleGraph<Integer, DefaultEdge> carbonGraph = new SimpleGraph<>(DefaultEdge.class);
		
		for (int i = 0 ; i < molecule.getNbNodes() ; i++) {
			carbonGraph.addVertex(i);
		}
		
		for (int i = 0 ; i < molecule.getNbNodes() ; i++) {
			for (int j = i + 1 ; j < molecule.getNbNodes() ; j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1) {
					carbonGraph.addEdge(i, j);
				}
			}
		}
		
		return carbonGraph;
	}
	
	public static SimpleGraph<Integer, DefaultEdge> buildHexagonGraph(Molecule molecule) {
		
		int [][] dualGraph = molecule.getDualGraph();
		SimpleGraph<Integer, DefaultEdge> hexagonGraph = new SimpleGraph<>(DefaultEdge.class);
		
		for (int i = 0 ; i < molecule.getNbHexagons() ; i++) {
			hexagonGraph.addVertex(i);
		}
		
		int [][] matrix = new int[molecule.getNbNodes()][molecule.getNbNodes()];
		
		for (int i = 0 ; i < molecule.getNbHexagons() ; i++) {
			for (int j = 0 ; j < 6 ; j++) {
				
				int v = dualGraph[i][j];
				
				if (v != -1 && matrix[i][v] == 0) {
					
					matrix[i][v] = 1;
					matrix[v][i] = 1;
					hexagonGraph.addEdge(i, v);
				}
				
			}
		}
		
		return hexagonGraph;
	}
	
	static public SimpleGraph<Integer, DefaultEdge> toCarbonGraph(SimpleGraph<Integer, DefaultEdge> hexagonGraph,
			int largeur) {
		int k = 0;
		int i, j;
		int haut, hautdroit, basdroit, bas, basgauche, hautgauche;
		SimpleGraph<Integer, DefaultEdge> carbonGraph = new SimpleGraph<>(DefaultEdge.class);

		for (j = 0; j < largeur; j++)
			for (i = 0; i < largeur; i++) {
				// numerotation des sommets
				if (j == 0) {
					haut = k;
					hautdroit = k + 1;
					basdroit = k + 2;
					bas = k + 3;
					if (i == 0) {
						basgauche = k + 4;
						hautgauche = k + 5;
					} else {
						basgauche = k - 4;
						hautgauche = k - 5;
					}
				} else {// j > 0
					if (i == 0) {
						haut = k + 4 - 6 * largeur;
						basgauche = k + 4;
						hautgauche = k + 5;
					} else {
						haut = k + 2 - 6 * (largeur + 1);
						basgauche = k - 4;
						hautgauche = k + 3 - 6 * (largeur + 1);
					}
					hautdroit = k + 3 - 6 * largeur;
					basdroit = k + 2;
					bas = k + 3;
				}

				if (hexagonGraph.containsVertex(i + largeur * j)) {
					carbonGraph.addVertex(haut);
					carbonGraph.addVertex(hautdroit);
					carbonGraph.addVertex(basdroit);
					carbonGraph.addVertex(bas);
					carbonGraph.addVertex(basgauche);
					carbonGraph.addVertex(hautgauche);
					carbonGraph.addEdge(haut, hautdroit);
					carbonGraph.addEdge(hautdroit, basdroit);
					carbonGraph.addEdge(basdroit, bas);
					carbonGraph.addEdge(bas, basgauche);
					carbonGraph.addEdge(basgauche, hautgauche);
					carbonGraph.addEdge(hautgauche, haut);
				}
				k = k + 6;
			}
		// System.out.println(carbonGraph);
		return carbonGraph;

	}

	static public String toCML(SimpleGraph<Integer, DefaultEdge> hexagonGraph, int largeur) {
		int k = 0;
		int i, j;
		int haut, hautdroit, basdroit, bas, basgauche, hautgauche;
		SimpleGraph<Integer, DefaultEdge> carbonGraph = new SimpleGraph<>(DefaultEdge.class);
		StringBuilder CMLVerticesString = new StringBuilder();
		StringBuilder CMLEdgesString = new StringBuilder();
		double dx = Math.sqrt(3.0) / 2.0;
		double facteur = 1.5;

		for (j = 0; j < largeur; j++)
			for (i = 0; i < largeur; i++) {
				// numerotation des sommets
				if (j == 0) {
					haut = k;
					hautdroit = k + 1;
					basdroit = k + 2;
					bas = k + 3;
					if (i == 0) {
						basgauche = k + 4;
						hautgauche = k + 5;
					} else {
						basgauche = k - 4;
						hautgauche = k - 5;
					}
				} else {// j > 0
					if (i == 0) {
						haut = k + 4 - 6 * largeur;
						basgauche = k + 4;
						hautgauche = k + 5;
					} else {
						haut = k + 2 - 6 * (largeur + 1);
						basgauche = k - 4;
						hautgauche = k + 3 - 6 * (largeur + 1);
					}
					hautdroit = k + 3 - 6 * largeur;
					basdroit = k + 2;
					bas = k + 3;
				}

				double xcentre = (-dx * j + 2 * dx * i) * facteur;
				double ycentre = 1.5 * j * facteur;

				if (hexagonGraph.containsVertex(i + largeur * j)) {
					CMLVerticesString.append(maybeNewCarbon(carbonGraph, haut, xcentre, ycentre - facteur));
					carbonGraph.addVertex(haut);
					CMLVerticesString.append(maybeNewCarbon(carbonGraph, hautdroit, xcentre + facteur * dx, ycentre - 0.5 * facteur));
					carbonGraph.addVertex(hautdroit);
					CMLVerticesString.append(maybeNewCarbon(carbonGraph, basdroit, xcentre + facteur * dx, ycentre + 0.5 * facteur));
					carbonGraph.addVertex(basdroit);
					CMLVerticesString.append(maybeNewCarbon(carbonGraph, bas, xcentre, ycentre + facteur));
					carbonGraph.addVertex(bas);
					CMLVerticesString.append(maybeNewCarbon(carbonGraph, basgauche, xcentre - dx * facteur, ycentre + 0.5 * facteur));
					carbonGraph.addVertex(basgauche);
					CMLVerticesString.append(maybeNewCarbon(carbonGraph, hautgauche, xcentre - dx * facteur, ycentre - 0.5 * facteur));
					carbonGraph.addVertex(hautgauche);
					CMLEdgesString.append(maybeNewBond(carbonGraph, haut, hautdroit));
					carbonGraph.addEdge(haut, hautdroit);
					CMLEdgesString.append(maybeNewBond(carbonGraph, hautdroit, basdroit));
					carbonGraph.addEdge(hautdroit, basdroit);
					CMLEdgesString.append(maybeNewBond(carbonGraph, basdroit, bas));
					carbonGraph.addEdge(basdroit, bas);
					CMLEdgesString.append(maybeNewBond(carbonGraph, bas, basgauche));
					carbonGraph.addEdge(bas, basgauche);
					CMLEdgesString.append(maybeNewBond(carbonGraph, basgauche, hautgauche));
					carbonGraph.addEdge(basgauche, hautgauche);
					CMLEdgesString.append(maybeNewBond(carbonGraph, hautgauche, haut));
					carbonGraph.addEdge(hautgauche, haut);
				}
				k = k + 6;
			}
		// System.out.println(carbonGraph);
		return "<molecule><atomArray>" + CMLVerticesString + "</atomArray><bondArray>" + CMLEdgesString
				+ "</bondArray><cycleClarArray /></molecule>";

	}

	private static String maybeNewCarbon(SimpleGraph<Integer, DefaultEdge> carbonGraph, int carbon, double x,
			double y) {
		if (!carbonGraph.containsVertex(carbon)) {
			return "<atom elementType=\"C\" id=\"a" + carbon + "\" x3=\"" + x + "\" y3=\"" + y + "\" z3=\"0\" />";
		} else
			return "";
	}

	private static String maybeNewBond(SimpleGraph<Integer, DefaultEdge> carbonGraph, int carbon1, int carbon2) {
		if (!carbonGraph.containsEdge(carbon1, carbon2)) {
			return "<bond atomRefs2=\"a" + carbon1 + " a" + carbon2 + "\" order=\"0\" />";
		} else
			return "";
	}
}
