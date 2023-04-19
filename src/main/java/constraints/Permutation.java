package constraints;

import utils.Coords;

/***
 * Définit les permutations possibles des indices de la liste des sommets du graphe d'hexagones
 * @author nicolasprcovic
 *
 */
public abstract class Permutation {
	private final int iteration;
	private final Coords center;
	private int largeur;


	public Permutation(int couronnes, int iteration){
		this.iteration = iteration;
		largeur = 2 * couronnes - 1;
		center = new Coords(couronnes - 1, couronnes - 1);
	}
	public Permutation(int couronnes) {
		this(couronnes, 0);
	}
	public Permutation(Coords center, int iteration) {
		this.iteration = iteration;
		this.center = center;
	}
	public Permutation(Coords center) {
		this(center, 0);
	}

	/***
	 * Apply the rotation to hex around the center
	 * @return the image of hex around the center
	 */
	public Coords rot(Coords hex) {
		for(int j = 0; j < iteration; j++)
			hex = rot60(hex);
		return hex;
	}

	/***
	 * Apply a 60 rotation to hex around the center
	 */
	public Coords rot60(Coords hex){
		return new Coords(hex.getY() - center.getY() + center.getX(), hex.getY() - hex.getX() + center.getX());
	}
	
	/***
	 * Apply a -60 rotation to hex around the center
	 */
	public Coords rot_60(Coords hex){
		return new Coords(hex.getX() + center.getY() - hex.getY(), hex.getX() + center.getY() - center.getX());
	}

	/***
	 * Apply an axis projection to hex with axis passing through the center of an hexagon
	 */
	public Coords hexAxis(Coords hex){
		for(int j = 0; j < iteration; j++)
			hex = rot60(hex);
		hex = new Coords(hex.getY() + center.getX() - center.getY(), hex.getX() - center.getX() + center.getY());
		for(int j = 0; j < iteration; j++)
			hex = rot_60(hex);
		return hex;
	}


	/***
	 * Apply an axis projection to hex with axis passing through an edge
	 */
	public Coords edgeAxis(Coords hex){
		for(int j = 0; j < iteration; j++)
			hex = rot120topVertex(hex);
		hex =  new Coords(
				- hex.getX() + 2 * center.getX() - center.getY() + hex.getY(), hex.getY());
		for(int j = 0; j < iteration; j++)
			hex = rot120topVertex(rot120topVertex(hex));
		return hex;
	}

	/***
	 * Apply a 120 rotation around the vertex at the top of the center
	 */
	public Coords rot120bottomVertex(Coords hex){
		return new Coords(hex.getY() - center.getY() - hex.getX() + 2 * center.getX(), - hex.getX() + center.getX() + 1 + center.getY());
	}

	/***
	 * Apply a 120 rotation around the vertex at the top of the center
	 */
	public Coords rot120topVertex(Coords hex){
		return new Coords(hex.getY() - center.getY() - hex.getX() + 2 * center.getX(), - hex.getX() + center.getX() - 1 + center.getY());
	}

	/***
	 * Apply a 180 rotation around the left edge of the hexagon center
	 */
	public Coords rot180edge(Coords hex){
		return new Coords(-1 - hex.getX() + 2 * center.getX(), - hex.getY() + 2 * center.getY());
	}

	/***
	 * Give the image of the permutation (coords)
	 */
	public abstract Coords from(Coords hex);
	
	public int from(int index) {
		Coords hex = new Coords(index % largeur, index / largeur);
		hex = from(hex);
		return hex.getX() + hex.getY() * largeur;

	}

	public Coords getCenter() {
		return center;
	}
	
	
}

