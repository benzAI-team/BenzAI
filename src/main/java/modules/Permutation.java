package modules;
/***
 * Définit les permutations possibles des indices de la liste des sommets du graphe d'hexagone
 * @author nicolasprcovic
 *
 */
public abstract class Permutation {
	private int couronnes;
	private int largeur;
	private int iteration;
	
	public Permutation(){}
	public Permutation(int couronnes, int iteration){
		this.couronnes = couronnes;
		this.iteration = iteration;
		largeur = couronnes * 2 - 1;
	}
	public Permutation(int couronnes) {
		this(couronnes, 0);
	}
	
	public int rot(int i) {
		for(int j = 0; j < iteration; j++)
			i = rot60(i);
		return i;
	}
	
	public int rot60(int i) {
		return largeur * (couronnes - 1 ) - (i % largeur) * largeur + (i / largeur) * (largeur + 1); 
	}
	public int diag(int i) {
		return i / largeur +  (i % largeur) * largeur;
	}
	
	public int vert(int i) {
		return (couronnes - 1) + (largeur + 1) * (i / largeur) - (i % largeur);
	}
	
	public int rot120vertex(int i) {
		return (largeur * (largeur - 1) - couronnes) - (largeur + 1) * (i % largeur) + (i / largeur);
	}
	
	public int rot180edge(int i) {
		return (4 * couronnes * (couronnes - 2) + 1) - largeur * (i / largeur) - (i % largeur);
	}
	
	public abstract int from(int i);
	
	
}

