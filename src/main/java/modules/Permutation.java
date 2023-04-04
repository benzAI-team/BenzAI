package modules;

import utils.Coords;
import utils.Couple;

/***
 * Définit les permutations possibles des indices de la liste des sommets du graphe d'hexagones
 * @author nicolasprcovic
 *
 */
public abstract class Permutation {
	private int couronnes;
	private int largeur;
	private int iteration;
	private Coords center;
	

	public Permutation(){}
	public Permutation(int couronnes, int iteration){
		this.couronnes = couronnes;
		this.iteration = iteration;
		largeur = couronnes * 2 - 1;
		center = new Coords(couronnes - 1, couronnes - 1);
	}
	public Permutation(int couronnes) {
		this(couronnes, 1);
	}
	public Permutation(Coords center, int iteration) {
		this.iteration = iteration;
		this.center = center;
	}
	public Permutation(Coords center) {
		this(center, 1);
	}
	
	/***
	 * Apply the (60/120/180) rotation to hexagon number i
	 * @param i
	 * @return image of i around the center of the coronenoid
	 */
	public int rot(int i) {
		for(int j = 0; j < iteration; j++)
			i = rot60(i);
		return i;
	}

	/***
	 * Apply the rotation to point around the center
	 * @param point
	 * @return the image of point around the center
	 */
	public Coords rot(Coords point) {
		for(int j = 0; j < iteration; j++)
			point = rot60(point);
		return point;
	}

	/***
	 * Apply a 60 rotation to hexagon index i
	 * @param i
	 * @return
	 */
	public int rot60(int i) {
		return largeur * (couronnes - 1 ) - (i % largeur) * largeur + (i / largeur) * (largeur + 1); 
	}
	
	/***
	 * Apply a 60 rotation to point around the center
	 * @param center
	 * @param point
	 * @return
	 */
	public Coords rot60(Coords point){
		return new Coords(point.getY() - center.getY() + center.getX(), point.getY() - point.getX() + center.getX());
	}
	
	/***
	 * Apply a -60 rotation to point around the center
	 * @param center
	 * @param point
	 * @return
	 */
	public Coords rot_60(Coords point){
		return new Coords(point.getX() + center.getY() - point.getY(), point.getX() + center.getY() - center.getX());
	}
	
	/***
	 * Apply a diagonal hexagonal axis projection to hexagon index i
	 * @param i
	 * @return
	 */
	public int diag(int i) {
		return i / largeur +  (i % largeur) * largeur;
	}
	
	/***

	/***
	 * Apply a hexagonal axis projection to point with diagonal passing through the center
	 * @param center
	 * @param point
	 * @return
	 */
	public Coords hexAxis(Coords point){
		for(int j = 0; j < iteration; j++)
			point = rot60(point);
		point = new Coords(point.getY() + center.getX() - center.getY(), point.getX() - center.getX() + center.getY());
		for(int j = 0; j < iteration; j++)
			point = rot_60(point);
		return point;
	}

	
	
	/***
	 * Apply an edge axis projection to hexagon index i
	 * @param i
	 * @return
	 */
	public int edgeAxis(int i) {
		return (couronnes - 1) + (largeur + 1) * (i / largeur) - (i % largeur);
	}

	/***
	 * Apply a vertical axis projection to point with axis passing through the center
	 * @param center
	 * @param point
	 * @return
	 */
	public Coords edgeAxis(Coords point){
		System.out.println("EdgeAxis:");
		for(int j = 0; j < iteration; j++)
			point = rot120vertex(point);
		point =  new Coords(- point.getX() + 2 * center.getX() - center.getY() + point.getY(), point.getY());
		for(int j = 0; j < iteration; j++)
			point = rot120vertex(rot120vertex(point));
		return point;
	}

	/***
	 * Apply a 120 rotation around the vertex at the top of the center
	 * @param i
	 * @return
	 */
	public int rot120vertex(int i) {
		return (largeur * (largeur - 1) - couronnes) - (largeur + 1) * (i % largeur) + (i / largeur);
	}
	/***
	 * Apply a 120 rotation around the vertex at the top of the center
	 * @param center
	 * @param point
	 * @return
	 */
	public Coords rot120vertex(Coords point){
		return new Coords(point.getY() - center.getY() - point.getX() + 2 * center.getX(), - point.getX() + center.getX() - 1 + center.getY());
	}

	/***
	 * Apply a 180 rotation around the left edge of the hexagon center
	 * @param i
	 * @return
	 */
	public int rot180edge(int i) {
		return (4 * couronnes * (couronnes - 2) + 1) - largeur * (i / largeur) - (i % largeur);
	}
	/***
	 * Apply a 180 rotation around the left edge of the hexagon center
	 * @param i
	 * @return
	 */
	public Coords rot180edge(Coords point){
		return new Coords(-1 - point.getX() + 2 * center.getX(), - point.getY() + 2 * center.getY());
	}

	/***
	 * Give the image of the permutation (coords)
	 * @param point
	 * @return
	 */
	public abstract Coords from(Coords point);
	
	public int from(int index) {
		Coords point = new Coords(index % largeur, index / largeur);
		point = from(point);
		return point.getX() + point.getY() * largeur;
		
	}
	
	/***
	 * getters
	 */
	public int getCouronnes() {
		return couronnes;
	}
	public int getLargeur() {
		return largeur;
	}
	public Coords getCenter() {
		return center;
	}
	
	
}

