package utils;

import java.awt.Point;
import java.util.Arrays;

public class RelativeMatrix {

	private final int [][] matrix;
	private final int width;
    private final int height;
    private final int xCenter;
    private final int yCenter;
	
	public RelativeMatrix(int width, int height, int xCenter, int yCenter) {
		matrix = new int[width][height];
		this.width = width;
		this.height = height;
		
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		
		for (int i = 0 ; i < width ; i++) {
			Arrays.fill(matrix[i], -1);
		}
	}
	
	/**
	 * Getters and setters
	 */
	
	public int[][] getMatrix() {
		return matrix;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	/**
	 * Class's methods
	 */
	
	public int get(int x, int y) {
		return matrix[xCenter + x][yCenter + y];
	}
	
	public void set(int x, int y, int value) {
		matrix[xCenter + x][yCenter + y] = value;
	}
	
	public Point find(int value) {
		for (int i = 0 ; i < width ; i++) {
			for (int j = 0 ; j < height ; j++) {
				if (matrix[i][j] == value)
					return new Point(i, j);
			}
		}
		return null;
	}
	
	public int maxValue() {
		int maxValue = - 1;
		for (int i = 0 ; i < width ; i++) {
			for (int j = 0 ; j < height ; j++) {
				if (matrix[i][j] > maxValue)
					maxValue = matrix[i][j];
			}
		}
		return maxValue;
	}
}
