package solveur.matrix_determinant;

public class MatrixMultiplication {
	/**
	 * Main method that initialises matrices we'll be counting with, calls
	 * method that does the multiplication and prints results
	 *
	 * @param args command line arguments; unused
	 */
	public static void main (String args[]) {
		
		int x[][] = {
			{1, 2, 3,},
			{4, 5, 6,},
			{7, 8, 9,},
		};
		
		int y[][] = {
			{9, 8, 7,},
			{6, 5, 4,},
			{3, 2, 1,},
		};
		
		int z[][] = MatrixOperations.multiplyMatrices (x, y);
		MatrixOperations.printMatrix (x, 1);
		MatrixOperations.printMatrix (y, 2);
		MatrixOperations.printMatrix (z, 3);
	}
}