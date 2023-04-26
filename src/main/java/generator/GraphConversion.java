package generator;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphConversion {

	static public SimpleGraph<Integer, DefaultEdge> toCarbonGraph(SimpleGraph<Integer, DefaultEdge> hexagonGraph, int largeur){
        int k = 0;
        int i, j;
        int haut, hautdroit, basdroit, bas, basgauche, hautgauche;
        SimpleGraph<Integer, DefaultEdge> carbonGraph = new SimpleGraph<>(DefaultEdge.class);
        
        for(j = 0 ; j < largeur ; j++)
            for(i = 0 ; i < largeur ; i++){
                // numerotation des sommets
                if(j == 0) {
                    haut = k;
                    hautdroit = k + 1;
                    basdroit = k + 2;
                    bas = k + 3;
                    if(i == 0) {
                    	basgauche = k + 4;
                    	hautgauche = k + 5;
                    }
                    else {
                    	basgauche = k - 4;
                    	hautgauche = k - 5;
                    }
                } 
                else {// j > 0
                	if(i == 0) {
                		haut = k + 4 - 6 * largeur;
                		basgauche = k + 4;
                		hautgauche = k + 5;
                	}
                	else {
                		haut = k + 2 - 6 * (largeur + 1);
                		basgauche = k - 4;
                		hautgauche = k + 3 - 6 * (largeur + 1);
                	}
                	hautdroit = k + 3 - 6 * largeur;
                	basdroit = k + 2;
                	bas = k + 3;
                }
                
                if(hexagonGraph.containsVertex(i + largeur * j)) {
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
        //System.out.println(carbonGraph);
        return carbonGraph;
		
	}
}
