package view.patterns;

import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import javafx.scene.Group;
import molecules.Node;
import utils.Couple;
import utils.HexNeighborhood;
import utils.RelativeMatrix;

import java.util.ArrayList;
import java.util.Arrays;

public class PatternGroup extends Group {

	private final PatternsEditionPane parent;
	private final int nbCrowns;
	private final int index;

	private int diameter;

	private int[][] displayedHexagons;
	private PatternHexagon[][] hexagons;

	private PatternHexagon center;

	private int degree;

	private ArrayList<PatternHexagon> border;
	private ArrayList<PatternHexagon> extendedBorder;

	PatternGroup(PatternsEditionPane parent, int nbCrowns, int index) {
		this.parent = parent;
		this.nbCrowns = nbCrowns;
		this.index = index;
		this.resize(500, 500);
		initialize();
	}

	private ArrayList<Double> getHexagonPoints(double xCenter, double yCenter) {

		ArrayList<Double> points = new ArrayList<>();

		points.add(xCenter);
		points.add(yCenter - 29.5);

		points.add(xCenter + 26.0);
		points.add(yCenter - 14.0);

		points.add(xCenter + 26.0);
		points.add(yCenter + 14.0);

		points.add(xCenter);
		points.add(yCenter + 29.5);

		points.add(xCenter - 26.0);
		points.add(yCenter + 14.0);

		points.add(xCenter - 26.0);
		points.add(yCenter - 14.0);

		return points;
	}

	private void initializeBorder() {

		border = new ArrayList<>();
		extendedBorder = new ArrayList<>();

		for (int i = 0; i < hexagons.length; i++) {
			if (i == 0 || i == hexagons.length - 1) {
				for (int j = 0; j < hexagons.length; j++) {
					if (hexagons[i][j] != null) {
						hexagons[i][j].setBorderAction();
						border.add(hexagons[i][j]);

						extendedBorder.add(hexagons[i][j]);
						if (j < nbCrowns && i == 0)
							extendedBorder.add(hexagons[i + 1][j + 1]);
						if (j > 0 && i == hexagons.length - 1)
							extendedBorder.add(hexagons[i - 1][j - 1]);
					}
				}
			}
			else {
				for (int j = 0; j < hexagons.length; j++) {
					if (hexagons[i][j] != null) {
						hexagons[i][j].setBorderAction();
						border.add(hexagons[i][j]);
						extendedBorder.add(hexagons[i][j]);

						if (i > 1 && i < hexagons.length - 2)
							extendedBorder.add(hexagons[i][j + 1]);
						break;
					}
				}

				for (int j = hexagons.length - 1; j >= 0; j--) {
					if (hexagons[i][j] != null) {
						hexagons[i][j].setBorderAction();
						border.add(hexagons[i][j]);
						extendedBorder.add(hexagons[i][j]);

						if (i > 1 && i < hexagons.length - 2)
							extendedBorder.add(hexagons[i][j - 1]);
						break;
					}
				}
			}
		}
	}

	private void buildDisplayedHexagons() {

		displayedHexagons = new int[diameter][diameter];

		int n = diameter - nbCrowns;

		int nCurrent = n;

		for (int x = 0; x < ((diameter - 1) / 2); x++) {
			for (int i = nCurrent; i >= 1; i--) {
				// enlever les i derniers
				for (int j = diameter - 1; j > diameter - 1 - i; j--)
					displayedHexagons[x][j] = -1;
			}
			nCurrent--;
		}

		nCurrent = 1;

		for (int x = ((diameter - 1) / 2) + 1; x < diameter; x++) {
			Arrays.fill(displayedHexagons[x], -1);
			nCurrent++;
		}
	}

	private ArrayList<Couple<Double, Double>> getFirstCenters() {

		ArrayList<Couple<Double, Double>> centers = new ArrayList<>();

		double x = 26 * nbCrowns + 50.0;
		double y = 26 * nbCrowns + 50.0;

		centers.add(new Couple<>(x, y));

		for (int line = 1; line <= ((diameter - 1) / 2); line++) {
			x -= 26.0;
			y += 43.5;
			centers.add(new Couple<>(x, y));
		}

		for (int line = ((diameter - 1) / 2) + 1; line < diameter; line++) {
			x += 26.0;
			y += 43.5;
			centers.add(new Couple<>(x, y));
		}

		return centers;
	}

	private void buildHexagons(ArrayList<Couple<Double, Double>> centers) {

		hexagons = new PatternHexagon[diameter][diameter];

		for (int line = 0; line < diameter; line++) {

			Couple<Double, Double> center = centers.get(line);
			double xCenter = center.getX();
			double yCenter = center.getY();

			for (int column = 0; column < diameter; column++) {
				if (displayedHexagons[line][column] == 0) {

					ArrayList<Double> points = getHexagonPoints(xCenter, yCenter);
					Couple<Integer, Integer> coords = new Couple<>(column, line);
					PatternHexagon hexagon = new PatternHexagon(this, coords, points);

					hexagons[line][column] = hexagon;

					xCenter += 52.0;
				}
			}
		}
	}

	private void drawHexagons(PatternHexagon[][] hexagons) {
		for (PatternHexagon[] hexagon : hexagons) {
			for (int j = 0; j < hexagons.length; j++) {
				if (hexagon[j] != null)
					this.getChildren().add(hexagon[j]);
			}
		}
	}

	private void initialize() {
		diameter = 2 * nbCrowns - 1;
		buildDisplayedHexagons();
		ArrayList<Couple<Double, Double>> centers = getFirstCenters();
		buildHexagons(centers);
		drawHexagons(hexagons);
		initializeBorder();
	}

	void setAllLabels(PatternLabel label) {
		for (int x = 0; x < diameter; x++) {
			for (int y = 0; y < diameter; y++) {
				if (hexagons[x][y] != null) {
					hexagons[x][y].setLabel(label);
				}
			}
		}
	}

	public boolean hasCenter() {
		return center != null;
	}

	private void removeCenter() {
		if (center != null) {
			center.disableCenter();
			center = null;
		}
	}

	public void disableOtherCenter(PatternHexagon hexagon) {
		removeCenter();
		center = hexagon;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	Pattern exportPattern() {
		int nbHexagons = computeHexagonNumber();

		ArrayList<Couple<Integer, Integer>> coordsArray = new ArrayList<>();
		int[][] hexagonCoordinates = new int[diameter][diameter];
		computeHexagonsCoordsIndex(coordsArray, hexagonCoordinates);

		int[][] adjacencyMatrix = computeAdjacencyMatrix(nbHexagons, hexagonCoordinates);

		RelativeMatrix relativeMatrix = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons,
				8 * nbHexagons);

		Node[] nodes = computeNodeArrayAndRelativeMatrix(nbHexagons, coordsArray, relativeMatrix);

		int[][] dualGraph = computeDualGraph(nbHexagons, hexagonCoordinates);

		PatternLabel[] labels = computeLabels(nbHexagons, coordsArray);

		Node centerNode = computeCenter(hexagonCoordinates, nodes);

		return new Pattern(adjacencyMatrix, labels, nodes, centerNode, dualGraph, degree);
	}

	private Node computeCenter(int[][] hexagonCoordinates, Node[] nodes) {
		Node centerNode;

		if (center != null)
			centerNode = nodes[hexagonCoordinates[center.getCoords().getX()][center.getCoords().getY()]];
		else
			centerNode = nodes[0];
		return centerNode;
	}

	private PatternLabel[] computeLabels(int nbHexagons, ArrayList<Couple<Integer, Integer>> coordsArray) {
		PatternLabel[] labels = new PatternLabel[nbHexagons];

		for (int i = 0; i < nbHexagons; i++) {
			Couple<Integer, Integer> coords = coordsArray.get(i);
			labels[i] = hexagons[coords.getY()][coords.getX()].getLabel();
		}
		return labels;
	}

	private int[][] computeDualGraph(int nbHexagons, int[][] hexagonCoordinates) {
		int[][] dualGraph = new int[nbHexagons][6];

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {

				if (hexagonCoordinates[i][j] != -1) {

					int u = hexagonCoordinates[i][j];

					Arrays.fill(dualGraph[u], -1);
					for(HexNeighborhood neighbor : HexNeighborhood.values()){
						int i2 = i + neighbor.dx();
						int j2 = j + neighbor.dy();
						if(i2 >= 0 && i2 <= diameter - 1 && j2 >= 0 && j2 <= diameter - 1)
							dualGraph[u][neighbor.getIndex()] = hexagonCoordinates[i2][j2];
					}
				}
			}
		}
		return dualGraph;
	}

	private static Node[] computeNodeArrayAndRelativeMatrix(int nbHexagons, ArrayList<Couple<Integer, Integer>> coordsArray, RelativeMatrix relativeMatrix) {
		int xShift = -coordsArray.get(0).getX();
		int yShift = -coordsArray.get(0).getY();

		Node[] nodes = new Node[nbHexagons];

		int indexNode = 0;
		for (Couple<Integer, Integer> couple : coordsArray) {

			int x = couple.getX() + xShift;
			int y = couple.getY() + yShift;

			nodes[indexNode] = new Node(x, y, indexNode);
			relativeMatrix.set(x, y, indexNode);

			indexNode++;
		}
		return nodes;
	}

	private int[][] computeAdjacencyMatrix(int nbHexagons, int[][] hexagonCoordinates) {
		int[][] matrix = new int[nbHexagons][nbHexagons];

		for (int x = 0; x < diameter; x++) {
			for (int y = 0; y < diameter; y++) {
				if (hexagonCoordinates[x][y] != -1) {
					int u = hexagonCoordinates[x][y];
					if (x > 0 && y > 0) {
						int v = hexagonCoordinates[x - 1][y - 1];
						if (v != -1) {
							matrix[u][v] = 1;
							matrix[v][u] = 1;
						}
					}
					if (y > 0) {
						int v = hexagonCoordinates[x][y - 1];
						if (v != -1) {
							matrix[u][v] = 1;
							matrix[v][u] = 1;
						}
					}

					if (x + 1 < diameter) {
						int v = hexagonCoordinates[x + 1][y];
						if (v != -1) {
							matrix[u][v] = 1;
							matrix[v][u] = 1;
						}
					}

					if (x + 1 < diameter && y + 1 < diameter) {
						int v = hexagonCoordinates[x + 1][y + 1];
						if (v != -1) {
							matrix[u][v] = 1;
							matrix[v][u] = 1;
						}
					}

					if (y + 1 < diameter) {
						int v = hexagonCoordinates[x][y + 1];
						if (v != -1) {
							matrix[u][v] = 1;
							matrix[v][u] = 1;
						}
					}

					if (x > 0) {
						int v = hexagonCoordinates[x - 1][y];
						if (v != -1) {
							matrix[u][v] = 1;
							matrix[v][u] = 1;
						}
					}
				}
			}
		}
		return matrix;
	}

	private void computeHexagonsCoordsIndex(ArrayList<Couple<Integer, Integer>> coordsArray, int[][] hexagonCoordinates) {
		int index = 0;

		for (int i = 0; i < diameter; i++)
			Arrays.fill(hexagonCoordinates[i], -1);

		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				if (hexagons[y][x] != null) {
					if (hexagons[y][x].isColored()) {
						hexagonCoordinates[x][y] = index;
						coordsArray.add(new Couple<>(x, y));
						index++;
					}
				}
			}
		}
	}

	private int computeHexagonNumber() {
		int nbHexagons = 0;
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (hexagons[i][j] != null) {
					if (hexagons[i][j].isColored())
						nbHexagons++;
				}
			}
		}
		return nbHexagons;
	}

	void importPattern(Pattern pattern) {

		for (int i = 0; i < pattern.getNbNodes(); i++) {

			Node node = pattern.getNode(i);

			int x = node.getY();
			int y = node.getX();

			PatternLabel label = pattern.getLabel(i);

			hexagons[x][y].setLabel(label);
		}

	}

	@SuppressWarnings("unchecked")
	public void fill() {

		ArrayList<PatternHexagon> hexagonsList = new ArrayList<>();
		for (int y = 0; y < diameter; y++)
			for (int x = 0; x < diameter; x++)
				if (hexagons[y][x] != null && hexagons[y][x].isColored())
						hexagonsList.add(hexagons[y][x]);

		Couple<Couple<Integer, Integer>, Couple<Integer, Integer>>[] toCheck = new Couple[hexagonsList.size()];

		for (int i = 0; i < hexagonsList.size(); i++) {
			PatternHexagon hexagon = hexagonsList.get(i);
			Couple<Integer, Integer> coords = hexagon.getCoords();

			int x = coords.getY();
			int y = coords.getX();

			int pos1 = 0, pos2 = 0, pos3 = 0;

			Couple<Integer, Integer> coordsPos1 = new Couple<>(x - 1, y);
			Couple<Integer, Integer> coordsPos2 = new Couple<>(x, y + 1);
			Couple<Integer, Integer> coordsPos3 = new Couple<>(x + 1, y + 1);
			Couple<Integer, Integer> coordsPos4 = new Couple<>(x + 1, y);
			Couple<Integer, Integer> coordsPos6 = new Couple<>(x - 1, y - 1);

			if (x > 0) {
				if (hexagons[x - 1][y] != null)
					if (hexagons[x - 1][y].isColored())
						pos1 = 1;
			}

			if (y < diameter - 1) {
				if (hexagons[x][y + 1] != null)
					if (hexagons[x][y + 1].isColored())
						pos2 = 1;
			}

			if (x < diameter - 1 && y < diameter - 1)
				if (hexagons[x + 1][y + 1].isColored())
					pos3 = 1;

			if (pos1 == 0 && pos2 == 0 && pos3 == 0) {
				// DO_NOTHING
			}

			else if (pos1 == 0 && pos2 == 0 && pos3 == 1)
				toCheck[i] = new Couple<>(coordsPos2, coordsPos4);

			else if (pos1 == 0 && pos2 == 1 && pos3 == 0)
				toCheck[i] = new Couple<>(coordsPos1, coordsPos3);

			else if (pos1 == 0 && pos2 == 1 && pos3 == 1) {
				// DO_NOTHING
			}

			else if (pos1 == 1 && pos2 == 0 && pos3 == 0)
				toCheck[i] = new Couple<>(coordsPos6, coordsPos2);

			else if (pos1 == 1 && pos2 == 0 && pos3 == 1)
				hexagons[coordsPos2.getX()][coordsPos2.getY()].setLabel(PatternLabel.NEUTRAL);

			else if (pos1 == 1 && pos2 == 1 && pos3 == 0) {
				// DO_NOTHING
			}

			else if (pos1 == 1 && pos2 == 1 && pos3 == 1) {
				// DO_NOTHING
			}
		}

		for (Couple<Couple<Integer, Integer>, Couple<Integer, Integer>> check : toCheck) {

			if (check != null) {

				int x1 = check.getX().getX();
				int y1 = check.getX().getY();

				int nbNeighbors1 = 0;

				if (x1 > 0) { // HIGH-RIGHT (1)
					if (hexagons[x1 - 1][y1] != null)
						if (hexagons[x1 - 1][y1].isColored())
							nbNeighbors1++;
				}

				if (y1 < diameter - 1) { // RIGHT (2)
					if (hexagons[x1][y1 + 1] != null)
						if (hexagons[x1][y1 + 1].isColored())
							nbNeighbors1++;
				}

				if (x1 < diameter - 1 && y1 < diameter - 1) { // DOWN_RIGHT (3)
					if (hexagons[x1 + 1][y1 + 1] != null)
						if (hexagons[x1 + 1][y1 + 1].isColored())
							nbNeighbors1++;
				}

				if (x1 < diameter - 1) { // DOWN_LEFT (4)
					if (hexagons[x1 + 1][y1] != null)
						if (hexagons[x1 + 1][y1].isColored())
							nbNeighbors1++;
				}

				if (y1 > 0) { // LEFT (5)
					if (hexagons[y1 - 1][x1] != null)
						if (hexagons[y1 - 1][x1].isColored())
							nbNeighbors1++;
				}

				if (x1 > 0 && y1 > 0) { // HIGH-LEFT (6)
					if (hexagons[x1 - 1][y1 - 1] != null)
						if (hexagons[x1 - 1][y1 - 1].isColored())
							nbNeighbors1++;
				}

				int x2 = check.getY().getX();
				int y2 = check.getY().getY();

				int nbNeighbors2 = 0;

				if (x2 > 0) { // HIGH-RIGHT (1)
					if (hexagons[x2 - 1][y2] != null)
						if (hexagons[x2 - 1][y2].isColored())
							nbNeighbors2++;
				}

				if (y2 < diameter - 1) { // RIGHT (2)
					if (hexagons[x2][y2 + 1] != null)
						if (hexagons[x2][y2 + 1].isColored())
							nbNeighbors2++;
				}

				if (x2 < diameter - 1 && y2 < diameter - 1) { // DOWN_RIGHT (3)
					if (hexagons[x2 + 1][y2 + 1] != null)
						if (hexagons[x2 + 1][y2 + 1].isColored())
							nbNeighbors2++;
				}

				if (x2 < diameter - 1) { // DOWN_LEFT (4)
					if (hexagons[x2 + 1][y2] != null)
						if (hexagons[x2 + 1][y2].isColored())
							nbNeighbors2++;
				}

				if (y2 > 0) { // LEFT (5)
					if (hexagons[y2 - 1][x2] != null)
						if (hexagons[y2 - 1][x2].isColored())
							nbNeighbors2++;
				}

				if (x2 > 0 && y2 > 0) { // HIGH-LEFT (6)
					if (hexagons[x2 - 1][y2 - 1] != null)
						if (hexagons[x2 - 1][y2 - 1].isColored())
							nbNeighbors2++;
				}

				if (nbNeighbors1 >= nbNeighbors2 && !hexagons[x1][y1].isColored())
					hexagons[x1][y1].setLabel(PatternLabel.NEUTRAL);

				else if (nbNeighbors1 < nbNeighbors2 && !hexagons[x2][y2].isColored())
					hexagons[x2][y2].setLabel(PatternLabel.NEUTRAL);
			}
		}
	}

	PatternsEditionPane getParentPane() {
		return parent;
	}

	ArrayList<PatternHexagon> getExtendedBorder() {
		return extendedBorder;
	}

	public ArrayList<PatternHexagon> getBorder() {
		return border;
	}

	public int getNbCrowns() {
		return nbCrowns;
	}

	public int getIndex() {
		return index;
	}

	PatternHexagon[][] getHexagonsMatrix() {
		return hexagons;
	}

}
