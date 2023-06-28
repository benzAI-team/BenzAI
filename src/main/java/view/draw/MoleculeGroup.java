package view.draw;

import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import javafx.scene.Group;
import benzenoid.Benzenoid;
import benzenoid.Node;
import utils.Couple;
import utils.HexNeighborhood;
import utils.RelativeMatrix;

import java.util.ArrayList;
import java.util.List;

public class MoleculeGroup extends Group {

	private final int nbCrowns;
	private int diameter;

	private int[][] displayedHexagons;
	private HexagonDraw[][] hexagons;

	private HexagonDraw center;

	private int degree;

	private final DrawBenzenoidPane drawPane;

	private ArrayList<HexagonDraw> border;
	private ArrayList<HexagonDraw> extendedBorder;

	public MoleculeGroup(int nbCrowns, DrawBenzenoidPane drawPane) {
		this.nbCrowns = nbCrowns;
		this.drawPane = drawPane;
		this.resize(500, 500);
		initialize();
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

	public HexagonDraw[][] getHexagonsMatrix() {
		return hexagons;
	}

	public DrawBenzenoidPane getDrawPane() {
		return drawPane;
	}

	public ArrayList<HexagonDraw> getHexagons() {
		ArrayList<HexagonDraw> hexagonsList = new ArrayList<>();

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				HexagonDraw hexagon = hexagons[i][j];
				if (hexagon != null)
					hexagonsList.add(hexagon);
			}
		}

		return hexagonsList;
	}

	public ArrayList<HexagonDraw> getBorder() {
		return border;
	}

	public ArrayList<HexagonDraw> getExtendedBorder() {
		return extendedBorder;
	}

	private ArrayList<Double> getHexagonPoints(double xCenter, double yCenter) {

		ArrayList<Double> points = new ArrayList<Double>();

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
			for (int i = 0; i < nCurrent; i++) {
				displayedHexagons[x][i] = -1;
			}
			nCurrent++;
		}
	}

	private ArrayList<Couple<Double, Double>> getFirstCenters() {

		ArrayList<Couple<Double, Double>> centers = new ArrayList<Couple<Double, Double>>();

		double x = 26 * nbCrowns + 50.0;
		double y = 26 * nbCrowns + 50.0;

		centers.add(new Couple<Double, Double>(x, y));

		for (int line = 1; line <= ((diameter - 1) / 2); line++) {
			x -= 26.0;
			y += 43.5;
			centers.add(new Couple<Double, Double>(x, y));
		}

		for (int line = ((diameter - 1) / 2) + 1; line < diameter; line++) {
			x += 26.0;
			y += 43.5;
			centers.add(new Couple<Double, Double>(x, y));
		}

		return centers;
	}

	private void buildHexagons(ArrayList<Couple<Double, Double>> centers) {

		hexagons = new HexagonDraw[diameter][diameter];

		for (int line = 0; line < diameter; line++) {

			Couple<Double, Double> center = centers.get(line);
			double xCenter = center.getX();
			double yCenter = center.getY();

			for (int column = 0; column < diameter; column++) {
				if (displayedHexagons[line][column] == 0) {

					ArrayList<Double> points = getHexagonPoints(xCenter, yCenter);
					Couple<Integer, Integer> coords = new Couple<Integer, Integer>(column, line);
					HexagonDraw hexagon = new HexagonDraw(this, coords, points);

					hexagons[line][column] = hexagon;

					xCenter += 52.0;
				}
			}
		}
	}

	private void drawHexagons(HexagonDraw[][] hexagons) {
		for (int i = 0; i < hexagons.length; i++) {
			for (int j = 0; j < hexagons.length; j++) {
				if (hexagons[i][j] != null)
					this.getChildren().add(hexagons[i][j]);
			}
		}
	}

	private void initialize() {

		diameter = 2 * nbCrowns - 1;

		buildDisplayedHexagons();
		ArrayList<Couple<Double, Double>> centers = getFirstCenters();
		buildHexagons(centers);
		initializeBorder();
		drawHexagons(hexagons);
	}

	public void setAllLabels(PatternLabel label) {
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

	public void removeCenter() {
		if (center != null) {
			center.disableCenter();
			center = null;
		}
	}

	public void disableOtherCenter(HexagonDraw hexagon) {
		removeCenter();
		center = hexagon;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	private List<Integer> computeNeighbors(RelativeMatrix coordsMatrix, Couple<Integer, Integer> coordsHexagon) {

		int x = coordsHexagon.getX();
		int y = coordsHexagon.getY();

		List<Integer> neighbors = new ArrayList<>();

		if (coordsMatrix.get(x, y - 1) != -1)
			neighbors.add(coordsMatrix.get(x, y - 1));

		if (coordsMatrix.get(x + 1, y) != -1)
			neighbors.add(coordsMatrix.get(x + 1, y));

		if (coordsMatrix.get(x + 1, y + 1) != -1)
			neighbors.add(coordsMatrix.get(x + 1, y + 1));

		if (coordsMatrix.get(x, y + 1) != -1)
			neighbors.add(coordsMatrix.get(x, y + 1));

		if (coordsMatrix.get(x - 1, y) != -1)
			neighbors.add(coordsMatrix.get(x - 1, y));

		if (coordsMatrix.get(x - 1, y - 1) != -1)
			neighbors.add(coordsMatrix.get(x - 1, y - 1));

		return neighbors;
	}

	private List<Integer> computeUncheckedNeighbors(RelativeMatrix coordsMatrix, Couple<Integer, Integer> coordsHexagon,
			int[] checkedHexagons) {

		int x = coordsHexagon.getX();
		int y = coordsHexagon.getY();

		List<Integer> neighbors = new ArrayList<>();

		if (coordsMatrix.get(x, y - 1) != -1 && checkedHexagons[coordsMatrix.get(x, y - 1)] == 0)
			neighbors.add(coordsMatrix.get(x, y - 1));

		if (coordsMatrix.get(x + 1, y) != -1 && checkedHexagons[coordsMatrix.get(x + 1, y)] == 0)
			neighbors.add(coordsMatrix.get(x + 1, y));

		if (coordsMatrix.get(x + 1, y + 1) != -1 && checkedHexagons[coordsMatrix.get(x + 1, y + 1)] == 0)
			neighbors.add(coordsMatrix.get(x + 1, y + 1));

		if (coordsMatrix.get(x, y + 1) != -1 && checkedHexagons[coordsMatrix.get(x, y + 1)] == 0)
			neighbors.add(coordsMatrix.get(x, y + 1));

		if (coordsMatrix.get(x - 1, y) != -1 && checkedHexagons[coordsMatrix.get(x - 1, y)] == 0)
			neighbors.add(coordsMatrix.get(x - 1, y));

		if (coordsMatrix.get(x - 1, y - 1) != -1 && checkedHexagons[coordsMatrix.get(x - 1, y - 1)] == 0)
			neighbors.add(coordsMatrix.get(x - 1, y - 1));

		return neighbors;
	}

	private void fillHolesOfSize1() {

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {

				HexagonDraw hexagon = hexagons[i][j];
				if (hexagon != null && hexagon.getLabel() == PatternLabel.VOID) {
					ArrayList<HexagonDraw> neighbors = getNeighbors(hexagon.getCoordinates());
					
					int nbNeighbors = 0;
					for (HexagonDraw neighbor : neighbors) {
						if (neighbor.getLabel() == PatternLabel.NEUTRAL)
							nbNeighbors ++;
							
					}
					
					if (nbNeighbors == 6)
						hexagon.setLabel(PatternLabel.NEUTRAL);
					
					/*
					boolean surrounded = true;
					
					for (HexagonDraw neighbor : neighbors) {
						
						if (neighbor.getLabel() == 0) {
							surrounded = false;
							break;
						}
						
					}
					if (surrounded) {
						hexagon.setLabel(1);
					}
					*/
				}
			}
		}
	}

	public Benzenoid exportMolecule() {

		fillHolesOfSize1();

		/*
		 * Computing the number of hexagons
		 */

		int nbHexagons = 0;

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (hexagons[i][j] != null) {

					if (hexagons[i][j].getLabel() != PatternLabel.VOID)
						nbHexagons++;
				}

			}
		}

		/*
		 * Connecting hexagons of each coords to an index
		 */

		ArrayList<Couple<Integer, Integer>> coordsArray = new ArrayList<>();
		int[][] hexagonCoordinates = new int[diameter][diameter];
		int index = 0;

		for (int i = 0; i < diameter; i++)
			for (int j = 0; j < diameter; j++)
				hexagonCoordinates[i][j] = -1;

		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				if (hexagons[y][x] != null) {
					if (hexagons[y][x].getLabel() != PatternLabel.VOID) {
						hexagonCoordinates[x][y] = index;
						coordsArray.add(new Couple<Integer, Integer>(x, y));
						index++;
					}
				}
			}
		}

		/*
		 * Creating the adjacency matrix
		 */

		int[][] hexagonsAdjacencyMatrix = new int[nbHexagons][nbHexagons];

		for (int x = 0; x < diameter; x++) {
			for (int y = 0; y < diameter; y++) {

				if (hexagonCoordinates[x][y] != -1) {

					int u = hexagonCoordinates[x][y];

					if (x > 0 && y > 0) {

						int v = hexagonCoordinates[x - 1][y - 1];
						if (v != -1) {
							hexagonsAdjacencyMatrix[u][v] = 1;
							hexagonsAdjacencyMatrix[v][u] = 1;
						}
					}

					if (y > 0) {

						int v = hexagonCoordinates[x][y - 1];
						if (v != -1) {
							hexagonsAdjacencyMatrix[u][v] = 1;
							hexagonsAdjacencyMatrix[v][u] = 1;
						}
					}

					if (x + 1 < diameter) {

						int v = hexagonCoordinates[x + 1][y];
						if (v != -1) {
							hexagonsAdjacencyMatrix[u][v] = 1;
							hexagonsAdjacencyMatrix[v][u] = 1;
						}
					}

					if (x + 1 < diameter && y + 1 < diameter) {

						int v = hexagonCoordinates[x + 1][y + 1];
						if (v != -1) {
							hexagonsAdjacencyMatrix[u][v] = 1;
							hexagonsAdjacencyMatrix[v][u] = 1;
						}
					}

					if (y + 1 < diameter) {

						int v = hexagonCoordinates[x][y + 1];
						if (v != -1) {
							hexagonsAdjacencyMatrix[u][v] = 1;
							hexagonsAdjacencyMatrix[v][u] = 1;
						}
					}

					if (x > 0) {

						int v = hexagonCoordinates[x - 1][y];
						if (v != -1) {
							hexagonsAdjacencyMatrix[u][v] = 1;
							hexagonsAdjacencyMatrix[v][u] = 1;
						}
					}
				}
			}
		}

		/*
		 * Creating nodes array and relativeMatrix
		 */

		RelativeMatrix coordsMatrix = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons,
				8 * nbHexagons);

		int xShift = -coordsArray.get(0).getX();
		int yShift = -coordsArray.get(0).getY();

		Node[] nodes = new Node[nbHexagons];

		int indexNode = 0;
		for (Couple<Integer, Integer> couple : coordsArray) {

			int x = couple.getX() + xShift;
			int y = couple.getY() + yShift;

			nodes[indexNode] = new Node(x, y, indexNode);
			coordsMatrix.set(x, y, indexNode);

			indexNode++;
		}

		/*
		 * Building Array of hexagons
		 */

		int[][] hexagons = new int[nbHexagons][6];
		int[] checkedHexagons = new int[nbHexagons];

		hexagons[0] = new int[] { 0, 1, 2, 3, 4, 5 };
		checkedHexagons[0] = 1;
		int indexHexagon = 6;

		ArrayList<Integer> candidats = new ArrayList<>();
		candidats.addAll(computeNeighbors(coordsMatrix, new Couple<>(nodes[0].getX(), nodes[0].getY())));

		while (candidats.size() > 0) {

			int candidat = candidats.get(0);

			int[] hexagon = new int[6];
			for (int i = 0; i < 6; i++)
				hexagon[i] = -1;

			int x = nodes[candidat].getX();
			int y = nodes[candidat].getY();

			// High-Right
			if (coordsMatrix.get(x, y - 1) != -1) {

				int neighbor = coordsMatrix.get(x, y - 1);

				if (checkedHexagons[neighbor] == 0) {

					if (hexagon[0] == -1)
						hexagon[0] = indexHexagon;

					if (hexagon[1] == -1)
						hexagon[1] = indexHexagon + 1;
					indexHexagon += 2;
				}

				else {

					int[] hexagon2 = hexagons[neighbor];
					hexagon[0] = hexagon2[4];
					hexagon[1] = hexagon2[3];
				}
			}

			// Right
			if (coordsMatrix.get(x + 1, y) != -1) {

				int neighbor = coordsMatrix.get(x + 1, y);

				if (checkedHexagons[neighbor] == 0) {

					if (hexagon[1] == -1)
						hexagon[1] = indexHexagon;

					if (hexagon[2] == -1)
						hexagon[2] = indexHexagon + 1;

					indexHexagon += 2;
				}

				else {

					int[] hexagon2 = hexagons[neighbor];
					hexagon[1] = hexagon2[5];
					hexagon[2] = hexagon2[4];
				}
			}

			// Down-Right
			if (coordsMatrix.get(x + 1, y + 1) != -1) {

				int neighbor = coordsMatrix.get(x + 1, y + 1);

				if (checkedHexagons[neighbor] == 0) {

					if (hexagon[2] == -1)
						hexagon[2] = indexHexagon;

					if (hexagon[3] == -1)
						hexagon[3] = indexHexagon + 1;
					indexHexagon += 2;
				}

				else {

					int[] hexagon2 = hexagons[neighbor];
					hexagon[2] = hexagon2[0];
					hexagon[3] = hexagon2[5];
				}
			}

			// Down-Left
			if (coordsMatrix.get(x, y + 1) != -1) {

				int neighbor = coordsMatrix.get(x, y + 1);

				if (checkedHexagons[neighbor] == 0) {

					if (hexagon[3] == -1)
						hexagon[3] = indexHexagon;

					if (hexagon[4] == -1)
						hexagon[4] = indexHexagon + 1;
					indexHexagon += 2;
				}

				else {

					int[] hexagon2 = hexagons[neighbor];
					hexagon[3] = hexagon2[1];
					hexagon[4] = hexagon2[0];
				}
			}

			// Left
			if (coordsMatrix.get(x - 1, y) != -1) {

				int neighbor = coordsMatrix.get(x - 1, y);

				if (checkedHexagons[neighbor] == 0) {

					if (hexagon[4] == -1)
						hexagon[4] = indexHexagon;

					if (hexagon[5] == -1)
						hexagon[5] = indexHexagon + 1;
					indexHexagon += 2;
				}

				else {

					int[] hexagon2 = hexagons[neighbor];
					hexagon[4] = hexagon2[2];
					hexagon[5] = hexagon2[1];
				}
			}

			// High-Left
			if (coordsMatrix.get(x - 1, y - 1) != -1) {

				int neighbor = coordsMatrix.get(x - 1, y - 1);

				if (checkedHexagons[neighbor] == 0) {

					if (hexagon[5] == -1)
						hexagon[5] = indexHexagon;

					if (hexagon[0] == -1)
						hexagon[0] = indexHexagon + 1;
					indexHexagon += 2;
				}

				else {

					int[] hexagon2 = hexagons[neighbor];
					hexagon[5] = hexagon2[3];
					hexagon[0] = hexagon2[2];
				}
			}

			for (int i = 0; i < 6; i++) {
				if (hexagon[i] == -1) {
					hexagon[i] = indexHexagon;
					indexHexagon++;
				}
			}

			// candidats.addAll(computeUncheckedNeighbors(coordsMatrix,
			// coordsArray.get(candidat), checkedHexagons));
			candidats.addAll(computeUncheckedNeighbors(coordsMatrix,
					new Couple<>(nodes[candidat].getX(), nodes[candidat].getY()), checkedHexagons));
			hexagons[candidat] = hexagon;
			checkedHexagons[candidat] = 1;
			candidats.remove(candidats.get(0));
		}

		/*
		 * Building nodes array and relative matrix
		 */

		int nbNodes = 0;
		int[] count = new int[indexHexagon];

		int[] nodesRefs = new int[indexHexagon];
		for (int i = 0; i < indexHexagon; i++)
			nodesRefs[i] = -1;

		for (int[] hexagon : hexagons) {

			for (int u : hexagon) {

				if (count[u] == 0) {
					nodesRefs[u] = nbNodes;
					count[u] = 1;
					nbNodes++;
				}
			}
		}

		Node[] atoms = new Node[nbNodes];

		checkedHexagons = new int[nbHexagons];
		checkedHexagons[0] = 1;

		atoms[nodesRefs[0]] = new Node(0, 0, 0);
		atoms[nodesRefs[1]] = new Node(1, 1, 1);
		atoms[nodesRefs[2]] = new Node(1, 2, 2);
		atoms[nodesRefs[3]] = new Node(0, 3, 3);
		atoms[nodesRefs[4]] = new Node(-1, 2, 4);
		atoms[nodesRefs[5]] = new Node(-1, 1, 5);

		candidats = new ArrayList<>();

		candidats.addAll(computeNeighbors(coordsMatrix, new Couple<>(nodes[0].getX(), nodes[0].getY())));

		indexNode = 6;

		while (candidats.size() > 0) {

			int candidat = candidats.get(0);

			int[] hexagon = hexagons[candidat];

			int x = nodes[candidat].getX();
			int y = nodes[candidat].getY();

			// High-Right
			if (coordsMatrix.get(x, y - 1) != -1 && checkedHexagons[coordsMatrix.get(x, y - 1)] == 1) {

				int neighbor = coordsMatrix.get(x, y - 1);

				int[] hexagon2 = hexagons[neighbor];

				int u = hexagon2[3];

				Node nu = atoms[nodesRefs[u]];

				atoms[nodesRefs[hexagon[2]]] = new Node(nu.getX(), nu.getY() + 1, nodesRefs[hexagon[2]]);
				indexNode++;

				atoms[nodesRefs[hexagon[3]]] = new Node(nu.getX() - 1, nu.getY() + 2, nodesRefs[hexagon[3]]);
				indexNode++;

				atoms[nodesRefs[hexagon[4]]] = new Node(nu.getX() - 2, nu.getY() + 1, nodesRefs[hexagon[4]]);
				indexNode++;

				atoms[nodesRefs[hexagon[5]]] = new Node(nu.getX() - 2, nu.getY(), nodesRefs[hexagon[5]]);
				indexNode++;

			}

			// Right
			if (coordsMatrix.get(x + 1, y) != -1 && checkedHexagons[coordsMatrix.get(x + 1, y)] == 1) {

				int neighbor = coordsMatrix.get(x + 1, y);

				int[] hexagon2 = hexagons[neighbor];

				int u = hexagon2[4];

				Node nu = atoms[nodesRefs[u]];

				atoms[nodesRefs[hexagon[3]]] = new Node(nu.getX() - 1, nu.getY() + 1, nodesRefs[hexagon[3]]);
				indexNode++;

				atoms[nodesRefs[hexagon[4]]] = new Node(nu.getX() - 2, nu.getY(), nodesRefs[hexagon[4]]);
				indexNode++;

				atoms[nodesRefs[hexagon[5]]] = new Node(nu.getX() - 2, nu.getY() - 1, nodesRefs[hexagon[5]]);
				indexNode++;

				atoms[nodesRefs[hexagon[0]]] = new Node(nu.getX() - 1, nu.getY() - 2, nodesRefs[hexagon[0]]);
				indexNode++;

			}

			// Down-Right
			if (coordsMatrix.get(x + 1, y + 1) != -1 && checkedHexagons[coordsMatrix.get(x + 1, y + 1)] == 1) {

				int neighbor = coordsMatrix.get(x + 1, y + 1);

				int[] hexagon2 = hexagons[neighbor];

				int u = hexagon2[5];

				Node nu = atoms[nodesRefs[u]];

				atoms[nodesRefs[hexagon[4]]] = new Node(nu.getX() - 1, nu.getY() - 1, nodesRefs[hexagon[4]]);
				indexNode++;

				atoms[nodesRefs[hexagon[5]]] = new Node(nu.getX() - 1, nu.getY() - 2, nodesRefs[hexagon[5]]);
				indexNode++;

				atoms[nodesRefs[hexagon[0]]] = new Node(nu.getX(), nu.getY() - 3, nodesRefs[hexagon[0]]);
				indexNode++;

				atoms[nodesRefs[hexagon[1]]] = new Node(nu.getX() + 1, nu.getY() - 2, nodesRefs[hexagon[1]]);
				indexNode++;

			}

			// Down-Left
			if (coordsMatrix.get(x, y + 1) != -1 && checkedHexagons[coordsMatrix.get(x, y + 1)] == 1) {

				int neighbor = coordsMatrix.get(x, y + 1);

				int[] hexagon2 = hexagons[neighbor];
				int u = hexagon2[0];

				Node nu = atoms[nodesRefs[u]];

				atoms[nodesRefs[hexagon[5]]] = new Node(nu.getX(), nu.getY() - 1, nodesRefs[hexagon[5]]);
				indexNode++;

				atoms[nodesRefs[hexagon[0]]] = new Node(nu.getX() + 1, nu.getY() - 2, nodesRefs[hexagon[0]]);
				indexNode++;

				atoms[nodesRefs[hexagon[1]]] = new Node(nu.getX() + 2, nu.getY() - 1, nodesRefs[hexagon[1]]);
				indexNode++;

				atoms[nodesRefs[hexagon[2]]] = new Node(nu.getX() + 2, nu.getY(), nodesRefs[hexagon[2]]);
				indexNode++;
			}

			// Left
			if (coordsMatrix.get(x - 1, y) != -1 && checkedHexagons[coordsMatrix.get(x - 1, y)] == 1) {

				int neighbor = coordsMatrix.get(x - 1, y);

				int[] hexagon2 = hexagons[neighbor];
				int u = hexagon2[1];

				Node nu = atoms[nodesRefs[u]];

				if (atoms[nodesRefs[hexagon[0]]] == null) {
					atoms[nodesRefs[hexagon[0]]] = new Node(nu.getX() + 1, nu.getY() - 1, nodesRefs[hexagon[0]]);
					indexNode++;
				}

				if (atoms[nodesRefs[hexagon[1]]] == null) {
					atoms[nodesRefs[hexagon[1]]] = new Node(nu.getX() + 2, nu.getY(), nodesRefs[hexagon[1]]);
					indexNode++;
				}

				if (atoms[nodesRefs[hexagon[2]]] == null) {
					atoms[nodesRefs[hexagon[2]]] = new Node(nu.getX() + 2, nu.getY() + 1, nodesRefs[hexagon[2]]);
					indexNode++;
				}

				if (atoms[nodesRefs[hexagon[3]]] == null) {
					atoms[nodesRefs[hexagon[3]]] = new Node(nu.getX() + 1, nu.getY() + 2, nodesRefs[hexagon[3]]);
					indexNode++;
				}

			}

			// High-Left
			if (coordsMatrix.get(x - 1, y - 1) != -1 && checkedHexagons[coordsMatrix.get(x - 1, y - 1)] == 1) {

				int neighbor = coordsMatrix.get(x - 1, y - 1);

				int[] hexagon2 = hexagons[neighbor];
				int u = hexagon2[2];

				Node nu = atoms[nodesRefs[u]];

				atoms[nodesRefs[hexagon[1]]] = new Node(nu.getX() + 1, nu.getY() + 1, nodesRefs[hexagon[1]]);
				indexNode++;

				atoms[nodesRefs[hexagon[2]]] = new Node(nu.getX() + 1, nu.getY() + 2, nodesRefs[hexagon[2]]);
				indexNode++;

				atoms[nodesRefs[hexagon[3]]] = new Node(nu.getX(), nu.getY() + 3, nodesRefs[hexagon[3]]);
				indexNode++;

				atoms[nodesRefs[hexagon[4]]] = new Node(nu.getX() - 1, nu.getY() + 2, nodesRefs[hexagon[4]]);
				indexNode++;

			}

			for (int i = 0; i < 6; i++) {
				if (hexagon[i] == -1) {
					hexagon[i] = indexHexagon;
					indexHexagon++;
				}
			}

			candidats.addAll(computeUncheckedNeighbors(coordsMatrix,
					new Couple<>(nodes[candidat].getX(), nodes[candidat].getY()), checkedHexagons));
			checkedHexagons[candidat] = 1;

			candidats.remove(candidats.get(0));
		}

		int[][] finalHexagons = new int[nbHexagons][6];

		for (int i = 0; i < nbHexagons; i++) {
			for (int j = 0; j < 6; j++) {
				finalHexagons[i][j] = nodesRefs[hexagons[i][j]];
			}
		}

		for (Node atom : atoms) {
			coordsMatrix.set(atom.getX(), atom.getY(), atom.getIndex());
		}

		int nbEdges = 0;
		int[][] adjacencyMatrix = new int[nbNodes][nbNodes];

		for (int[] hexagon : finalHexagons) {
			for (int i = 0; i < 6; i++) {

				int u = hexagon[i];
				int v = hexagon[(i + 1) % 6];

				if (adjacencyMatrix[u][v] == 0) {

					adjacencyMatrix[u][v] = 1;
					adjacencyMatrix[v][u] = 1;
					nbEdges++;
				}
			}
		}

		return new Benzenoid(nbNodes, nbEdges, nbHexagons, finalHexagons, atoms, adjacencyMatrix, coordsMatrix);
	}

	public Pattern exportFragment() {

		/*
		 * Computing the number of hexagons
		 */

		int nbHexagons = 0;

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (hexagons[i][j] != null) {

					if (hexagons[i][j].getLabel() != PatternLabel.VOID)
						nbHexagons++;
				}

			}
		}

		/*
		 * Connecting hexagons of each coords to an index
		 */

		ArrayList<Couple<Integer, Integer>> coordsArray = new ArrayList<>();
		int[][] hexagonCoordinates = new int[diameter][diameter];
		int index = 0;

		for (int i = 0; i < diameter; i++)
			for (int j = 0; j < diameter; j++)
				hexagonCoordinates[i][j] = -1;

		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				if (hexagons[y][x] != null) {
					if (hexagons[y][x].getLabel() != PatternLabel.VOID) {
						hexagonCoordinates[x][y] = index;
						coordsArray.add(new Couple<Integer, Integer>(x, y));
						index++;
					}
				}
			}
		}

		/*
		 * Creating the adjacency matrix
		 */

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

		/*
		 * Creating nodes array and relativeMatrix
		 */

		RelativeMatrix relativeMatrix = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons,
				8 * nbHexagons);

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

		/*
		 * Creating dual graph
		 */

		int[][] dualGraph = new int[nbHexagons][6];

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {

				if (hexagonCoordinates[i][j] != -1) {

					int u = hexagonCoordinates[i][j];

					for (int k = 0; k < 6; k++)
						dualGraph[u][k] = -1;

					for(HexNeighborhood  neighbor : HexNeighborhood.values()){
						int i2 = i + neighbor.dx();
						int j2 = j + neighbor.dy();
						if(i2 >= 0 && i2 <= diameter - 1 && j2 >= 0 && j2 >= diameter - 1)
							dualGraph[u][neighbor.getIndex()] = hexagonCoordinates[i2][j2];
					}
//					if (j > 0)
//						dualGraph[u][0] = hexagonCoordinates[i][j - 1];
//
//					if (i < diameter - 1)
//						dualGraph[u][1] = hexagonCoordinates[i + 1][j];
//
//					if (i < diameter - 1 && j < diameter - 1)
//						dualGraph[u][2] = hexagonCoordinates[i + 1][j + 1];
//
//					if (j < diameter - 1)
//						dualGraph[u][3] = hexagonCoordinates[i][j + 1];
//
//					if (i > 0)
//						dualGraph[u][4] = hexagonCoordinates[i - 1][j];
//
//					if (i > 0 && j > 0)
//						dualGraph[u][5] = hexagonCoordinates[i - 1][j - 1];
				}
			}
		}

		/*
		 * Creating labels
		 */

		PatternLabel[] labels = new PatternLabel[nbHexagons];

		for (int i = 0; i < nbHexagons; i++) {
			Couple<Integer, Integer> coords = coordsArray.get(i);
			labels[i] = hexagons[coords.getY()][coords.getX()].getLabel();
		}

		/*
		 * Creating center
		 */

		Node centerNode;

		if (center != null)
			centerNode = nodes[hexagonCoordinates[center.getCoordinates().getX()][center.getCoordinates().getY()]];
		else
			centerNode = nodes[0];

		return new Pattern(matrix, labels, nodes, centerNode, dualGraph, degree);
	}

	public void importPattern(Pattern pattern) {

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

		ArrayList<HexagonDraw> hexagonsList = new ArrayList<HexagonDraw>();

		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				if (hexagons[y][x] != null) {
					if (hexagons[y][x].getLabel() != PatternLabel.VOID) {
						hexagonsList.add(hexagons[y][x]);
					}
				}
			}
		}

		Couple<Couple<Integer, Integer>, Couple<Integer, Integer>>[] toCheck = new Couple[hexagonsList.size()];

		for (int i = 0; i < hexagonsList.size(); i++) {

			HexagonDraw hexagon = hexagonsList.get(i);
			Couple<Integer, Integer> coords = hexagon.getCoordinates();

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
					if (hexagons[x - 1][y].getLabel() != PatternLabel.VOID)
						pos1 = 1;
			}

			if (y < diameter - 1) {
				if (hexagons[x][y + 1] != null)
					if (hexagons[x][y + 1].getLabel() != PatternLabel.VOID)
						pos2 = 1;
			}

			if (x < diameter - 1 && y < diameter - 1)
				if (hexagons[x + 1][y + 1].getLabel() != PatternLabel.VOID)
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

		for (int i = 0; i < toCheck.length; i++) {

			Couple<Couple<Integer, Integer>, Couple<Integer, Integer>> check = toCheck[i];
			if (check != null) {

				int x1 = check.getX().getX();
				int y1 = check.getX().getY();

				int nbNeighbors1 = 0;

				if (x1 > 0) { // HIGH-RIGHT (1)
					if (hexagons[x1 - 1][y1] != null)
						if (hexagons[x1 - 1][y1].getLabel() != PatternLabel.VOID)
							nbNeighbors1++;
				}

				if (y1 < diameter - 1) { // RIGHT (2)
					if (hexagons[x1][y1 + 1] != null)
						if (hexagons[x1][y1 + 1].getLabel() != PatternLabel.VOID)
							nbNeighbors1++;
				}

				if (x1 < diameter - 1 && y1 < diameter - 1) { // DOWN_RIGHT (3)
					if (hexagons[x1 + 1][y1 + 1] != null)
						if (hexagons[x1 + 1][y1 + 1].getLabel() != PatternLabel.VOID)
							nbNeighbors1++;
				}

				if (x1 < diameter - 1) { // DOWN_LEFT (4)
					if (hexagons[x1 + 1][y1] != null)
						if (hexagons[x1 + 1][y1].getLabel() != PatternLabel.VOID)
							nbNeighbors1++;
				}

				if (y1 > 0) { // LEFT (5)
					if (hexagons[y1 - 1][x1] != null)
						if (hexagons[y1 - 1][x1].getLabel() != PatternLabel.VOID)
							nbNeighbors1++;
				}

				if (x1 > 0 && y1 > 0) { // HIGH-LEFT (6)
					if (hexagons[x1 - 1][y1 - 1] != null)
						if (hexagons[x1 - 1][y1 - 1].getLabel() != PatternLabel.VOID)
							nbNeighbors1++;
				}

				int x2 = check.getY().getX();
				int y2 = check.getY().getY();

				int nbNeighbors2 = 0;

				if (x2 > 0) { // HIGH-RIGHT (1)
					if (hexagons[x2 - 1][y2] != null)
						if (hexagons[x2 - 1][y2].getLabel() != PatternLabel.VOID)
							nbNeighbors2++;
				}

				if (y2 < diameter - 1) { // RIGHT (2)
					if (hexagons[x2][y2 + 1] != null)
						if (hexagons[x2][y2 + 1].getLabel() != PatternLabel.VOID)
							nbNeighbors2++;
				}

				if (x2 < diameter - 1 && y2 < diameter - 1) { // DOWN_RIGHT (3)
					if (hexagons[x2 + 1][y2 + 1] != null)
						if (hexagons[x2 + 1][y2 + 1].getLabel() != PatternLabel.VOID)
							nbNeighbors2++;
				}

				if (x2 < diameter - 1) { // DOWN_LEFT (4)
					if (hexagons[x2 + 1][y2] != null)
						if (hexagons[x2 + 1][y2].getLabel() != PatternLabel.VOID)
							nbNeighbors2++;
				}

				if (y2 > 0) { // LEFT (5)
					if (hexagons[y2 - 1][x2] != null)
						if (hexagons[y2 - 1][x2].getLabel() != PatternLabel.VOID)
							nbNeighbors2++;
				}

				if (x2 > 0 && y2 > 0) { // HIGH-LEFT (6)
					if (hexagons[x2 - 1][y2 - 1] != null)
						if (hexagons[x2 - 1][y2 - 1].getLabel() != PatternLabel.VOID)
							nbNeighbors2++;
				}

				if (nbNeighbors1 >= nbNeighbors2 && hexagons[x1][y1].getLabel() == PatternLabel.VOID)
					hexagons[x1][y1].setLabel(PatternLabel.NEUTRAL);

				else if (nbNeighbors1 < nbNeighbors2 && hexagons[x2][y2].getLabel() == PatternLabel.VOID)
					hexagons[x2][y2].setLabel(PatternLabel.NEUTRAL);
			}
		}
	}

	public int getNbHexagons() {
		int nbHexagons = 0;
		for (int i = 0; i < hexagons.length; i++) {
			for (int j = 0; j < hexagons.length; j++) {
				if (hexagons[i][j].getLabel() == PatternLabel.NEUTRAL)
					nbHexagons++;
			}
		}
		return nbHexagons;
	}

	public ArrayList<HexagonDraw> getNeighbors(Couple<Integer, Integer> coords) {

		int j = coords.getX();
		int i = coords.getY();

		ArrayList<HexagonDraw> neighbors = new ArrayList<>();

		if (j > 0 && hexagons[i][j - 1] != null)
			neighbors.add(hexagons[i][j - 1]);

		if (i + 1 < diameter && hexagons[i + 1][j] != null)
			neighbors.add(hexagons[i + 1][j]);

		if (i + 1 < diameter && j + 1 < diameter && hexagons[i + 1][j + 1] != null)
			neighbors.add(hexagons[i + 1][j + 1]);

		if (j + 1 < diameter && hexagons[i][j + 1] != null)
			neighbors.add(hexagons[i][j + 1]);

		if (i > 0 && hexagons[i - 1][j] != null)
			neighbors.add(hexagons[i - 1][j]);

		if (i > 0 && j > 0 && hexagons[i - 1][j - 1] != null)
			neighbors.add(hexagons[i - 1][j - 1]);

		return neighbors;
	}
}
