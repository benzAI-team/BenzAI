package solveur.matrix_determinant;

import java.text.MessageFormat;

public enum MatrixDeterminant {
    ;

    /**
	 * Main method that initializes the matrix and calls method to calculate
	 * its determinant
	 *
	 * @param args command line arguments; unused
	 */
public static void main (String[] args) {
double determinant;
double[][] x = {
				{10, 15, 7,},
				{20, 57, 30,},
				{37, 56, 85,},
		};
		determinant = MatrixOperations.matrixDeterminant (x);
System.out.println (MessageFormat.format ("Determinant: {0}", Double.toString (determinant)));
	}
}
