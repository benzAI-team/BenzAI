package generator;

import generator.patterns.Pattern;
import molecules.Node;
import utils.Couple;
import utils.HexNeighborhood;

import java.util.ArrayList;
import java.util.Collections;

public class Solution {

	private final ArrayList<Integer> vertices;
	private final int[] correspondancesHexagons; // avec variables hors coro -> sans
	private final int[] hexagonsCorrespondances;// sans variables hors coro -> avec
	private final int[][] coordsMatrixCoronenoid;
	private final int coronenoidCenter;
	private final Node[] coronenoidNodes;
	private final int nbCrowns;

	private Pattern pattern;

	public Solution(Node[] coronenoidNodes, int[] correspondancesHexagons, int[] hexagonsCorrespondances,
			int[][] coordsMatrixCoronenoid, int coronenoidCenter, int nbCrowns, ArrayList<Integer> vertices) {
		this.coronenoidNodes = coronenoidNodes;
		this.correspondancesHexagons = correspondancesHexagons;
		this.hexagonsCorrespondances = hexagonsCorrespondances;
		this.coordsMatrixCoronenoid = coordsMatrixCoronenoid;
		this.coronenoidCenter = coronenoidCenter;
		this.nbCrowns = nbCrowns;
		this.vertices = vertices;
		test();
	}

	private void test() {

	}

	public ArrayList<Integer> getVertices() {
		return vertices;
	}

	public int getVertex(int index) {
		return vertices.get(index);
	}

	public Node getCoronenoidNode(int index) {
		return coronenoidNodes[index];
	}

	public int getNbNodes() {
		return vertices.size();
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public ArrayList<Integer> rotation180() {

		Node center = getCoronenoidNode(coronenoidCenter);

		int xc = center.getX();
		int yc = center.getX();

		ArrayList<Integer> rotation = new ArrayList<>();

		for (int i = 0; i < vertices.size(); i++) {

			int vertex = getVertex(i);
			int x1 = getCoronenoidNode(vertex).getX();
			int y1 = getCoronenoidNode(vertex).getY();

			int xd = xc - x1;
			int yd = yc - y1;

			int x2 = xc + xd;
			int y2 = yc + yd;

			int image = correspondancesHexagons[coordsMatrixCoronenoid[x2][y2]];
			rotation.add(image);
		}

		return rotation;
	}

	public ArrayList<ArrayList<Integer>> translationsFaceMirror() {

		int diameter = coordsMatrixCoronenoid.length;
		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();

		ArrayList<Integer> rotation = rotation180();

		for (int shift = -1 * diameter; shift <= diameter; shift++) {

			ArrayList<Integer> translation1 = new ArrayList<>();
			ArrayList<Integer> translation2 = new ArrayList<>();

			boolean embedded1 = true;
			boolean embedded2 = true;

			for (int vertexIndex1 : vertices) {

				Node n1 = coronenoidNodes[vertexIndex1];
				int x1 = n1.getX() + shift;
				int y1 = n1.getY() + shift;

				if (!(x1 >= 0 && x1 < diameter && y1 >= 0 && y1 < diameter) || coordsMatrixCoronenoid[x1][y1] == -1) {
					embedded1 = false;
					break;
				}

				translation1.add(correspondancesHexagons[coordsMatrixCoronenoid[x1][y1]]);

			}

			if (embedded1) {
				Collections.sort(translation1);
				if (!translations.contains(translation1)) {
					translations.add(translation1);
				}
			}

			for (int i = 0; i < vertices.size(); i++) {

				int vertexIndex2 = rotation.get(i);

				Node n2 = coronenoidNodes[vertexIndex2];

				int x2 = n2.getX() + shift;
				int y2 = n2.getY() + shift;

				if (!(x2 >= 0 && x2 < diameter && y2 >= 0 && y2 < diameter) || coordsMatrixCoronenoid[x2][y2] == -1) {
					embedded2 = false;
					break;
				}

				translation2.add(correspondancesHexagons[coordsMatrixCoronenoid[x2][y2]]);

			}

			if (embedded2) {
				Collections.sort(translation2);
				if (!translations.contains(translation2)) {
					translations.add(translation2);
				}
			}
		}

		return translations;
	}

	public ArrayList<ArrayList<Integer>> translationsEdgeMirror() {

		int diameter = coordsMatrixCoronenoid.length;
		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();

		ArrayList<Integer> rotation = rotation180();

		for (int shift = (-1 * diameter); shift <= diameter; shift++) {
			if (shift % 2 == 0) {

				ArrayList<Integer> translation1 = new ArrayList<>();
				ArrayList<Integer> translation2 = new ArrayList<>();

				boolean embedded1 = true;
				boolean embedded2 = true;

				for (int vertexIndex1 : vertices) {

					Node n1 = coronenoidNodes[vertexIndex1];
					int x1 = n1.getX() + shift;
					int y1 = n1.getY() + shift / 2;

					if (!(x1 >= 0 && x1 < diameter && y1 >= 0 && y1 < diameter)
							|| coordsMatrixCoronenoid[x1][y1] == -1) {
						embedded1 = false;
						break;
					}

					translation1.add(correspondancesHexagons[coordsMatrixCoronenoid[x1][y1]]);

				}

				if (embedded1) {
					Collections.sort(translation1);
					if (!translations.contains(translation1)) {
						translations.add(translation1);
					}
				}

				for (int i = 0; i < vertices.size(); i++) {

					int vertexIndex2 = rotation.get(i);

					Node n2 = coronenoidNodes[vertexIndex2];

					int x2 = n2.getX() + shift;
					int y2 = n2.getY() + shift / 2;

					if (!(x2 >= 0 && x2 < diameter && y2 >= 0 && y2 < diameter)
							|| coordsMatrixCoronenoid[x2][y2] == -1) {
						embedded2 = false;
						break;
					}

					translation2.add(correspondancesHexagons[coordsMatrixCoronenoid[x2][y2]]);

				}

				if (embedded2) {
					Collections.sort(translation2);
					if (!translations.contains(translation2)) {
						translations.add(translation2);
					}
				}
			}
		}

		return translations;
	}

	public ArrayList<ArrayList<Integer>> borderTranslations(ArrayList<Integer> topBorder,
			ArrayList<Integer> leftBorder) {

		ArrayList<ArrayList<Integer>> borderTranslations = new ArrayList<>();
		ArrayList<ArrayList<Integer>> translations = allTranslations();

		for (ArrayList<Integer> translation : translations) {
			boolean touchTop = false;
			boolean touchLeft = false;

			for (Integer i : translation) {
				if (topBorder.contains(i))
					touchTop = true;
				if (leftBorder.contains(i))
					touchLeft = true;
				if (touchTop && touchLeft) {
					borderTranslations.add(translation);
					break;
				}
			}
		}

		return borderTranslations;
	}

	public ArrayList<ArrayList<Integer>> allTranslations() {

		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();
		int diameter = coordsMatrixCoronenoid.length;

		ArrayList<ArrayList<Integer>> rotations = allRotations();

		for (ArrayList<Integer> rotation : rotations) {

			for (int xShift = -diameter; xShift <= diameter; xShift++) {
				for (int yShift = -diameter; yShift <= diameter; yShift++) {

					ArrayList<Integer> translation = new ArrayList<>();
					boolean embedded = true;

					for (int vertexIndex : rotation) {
						Node node = coronenoidNodes[vertexIndex];

						int x = node.getX() + xShift;
						int y = node.getY() + yShift;

						if (!(x >= 0 && x < diameter && y >= 0 && y < diameter) || coordsMatrixCoronenoid[x][y] == -1) {
							embedded = false;
							break;
						}

						translation.add(correspondancesHexagons[coordsMatrixCoronenoid[x][y]]);
					}

					if (embedded) {
						Collections.sort(translation);
						if (!translations.contains(translation))
							translations.add(translation);
					}
				}
			}

		}

		return translations;

	}

	public ArrayList<ArrayList<Integer>> allRotations() {

		ArrayList<ArrayList<Integer>> rotations = new ArrayList<>();
		ArrayList<ArrayList<Integer>> translatedRotations = new ArrayList<>();

		/*
		 * Simple rotations
		 */

		int diameter = coordsMatrixCoronenoid.length;

		ArrayList<Integer> initialRotation = new ArrayList<>();
		for (Integer vertex : vertices)
			initialRotation.add(hexagonsCorrespondances[vertex]);

		rotations.add(initialRotation);
		translatedRotations.add(vertices);

		for (int i = 1; i < 6; i++) {

			ArrayList<Integer> lastRotation = rotations.get(rotations.size() - 1);
			ArrayList<Integer> newRotation = new ArrayList<>();
			ArrayList<Integer> translatedRotation = new ArrayList<>();

			for (Integer vertex : lastRotation) {
				int newVertex = Solution.rotation60(diameter, nbCrowns, vertex);
				newRotation.add(newVertex);
				translatedRotation.add(correspondancesHexagons[newVertex]);
			}

			rotations.add(newRotation);
			translatedRotations.add(translatedRotation);
		}

		/*
		 * Mirror rotations
		 */

		Pattern mirrorPattern = pattern.mirror();
		ArrayList<Integer> mirror = placePattern(mirrorPattern);

		ArrayList<Integer> initialRotation2 = new ArrayList<>();
		assert mirror != null;
		for (Integer vertex : mirror)
			initialRotation2.add(hexagonsCorrespondances[vertex]);

		rotations.add(initialRotation2);
		translatedRotations.add(mirror);

		for (int i = 1; i < 6; i++) {

			ArrayList<Integer> lastRotation = rotations.get(rotations.size() - 1);
			ArrayList<Integer> newRotation = new ArrayList<>();
			ArrayList<Integer> translatedRotation = new ArrayList<>();

			for (Integer vertex : lastRotation) {
				int newVertex = Solution.rotation60(diameter, nbCrowns, vertex);
				newRotation.add(newVertex);
				translatedRotation.add(correspondancesHexagons[newVertex]);
			}

			rotations.add(newRotation);
			translatedRotations.add(translatedRotation);
		}

		return translatedRotations;

	}

	private ArrayList<Integer> placePattern(Pattern pattern) {

		/*
		 * Trouver l'hexagone pr�sent du fragment le plus en haut � gauche
		 */

		int diameter = coordsMatrixCoronenoid.length;

		int minY = Integer.MAX_VALUE;
		for (Node node : pattern.getNodesRefs())
			if (node.getY() < minY)
				minY = node.getY();

		while (true) {

			boolean containsPresentHexagon = false;
			for (int i = 0; i < pattern.getNodesRefs().length; i++) {
				Node node = pattern.getNodesRefs()[i];
				if (node.getY() == minY)
					containsPresentHexagon = true;
			}

			if (containsPresentHexagon)
				break;

			minY++;
		}

		int nodeIndex = -1;
		int minX = Integer.MAX_VALUE;
		for (int i = 0; i < pattern.getNodesRefs().length; i++) {
			Node node = pattern.getNodesRefs()[i];
			if (node.getY() == minY && node.getX() < minX) {
				minX = node.getX();
				nodeIndex = i;
			}
		}

		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				int hexagon = coordsMatrixCoronenoid[y][x];
				if (hexagon != -1) {

					/*
					 * On place le fragment dans le coron�no�de de telle sorte que firstNode
					 * corresponde � hexagon
					 */

					int[] checkedHexagons = new int[pattern.getNodesRefs().length];
					Couple<Integer, Integer>[] coords = new Couple[pattern.getNodesRefs().length];

					int candidat = nodeIndex;
					checkedHexagons[nodeIndex] = 1;
					coords[nodeIndex] = new Couple<>(x, y);

					ArrayList<Integer> candidats = new ArrayList<>();

					for (HexNeighborhood neighbor : HexNeighborhood.values()) {
						if (pattern.getNeighborGraph()[candidat][neighbor.getIndex()] != -1) {
							int neighborIndex = pattern.getNeighborGraph()[candidat][neighbor.getIndex()];
							candidats.add(neighborIndex);
							coords[neighborIndex] = new Couple<>(x + neighbor.dx(), y + neighbor.dy());
							checkedHexagons[neighborIndex] = 1;
						}
					}

					while (candidats.size() > 0) {

						candidat = candidats.get(0);

						for (HexNeighborhood neighbor : HexNeighborhood.values()) {
							if (pattern.getNeighborGraph()[candidat][neighbor.getIndex()] != -1) {
								int neighborIndex = pattern.getNeighborGraph()[candidat][neighbor.getIndex()];
								if (checkedHexagons[neighborIndex] == 0) {
									candidats.add(neighborIndex);
									coords[neighborIndex] = new Couple<>(coords[candidat].getX() + neighbor.dx(), coords[candidat].getY() + neighbor.dy());
									checkedHexagons[neighborIndex] = 1;
								}
							}
						}

						candidats.remove(candidats.get(0));
					}

					/*
					 * On teste si le fragment obtenu est valide
					 */

					boolean valid = true;
					for (Couple<Integer, Integer> coord : coords) {
						if (!isValid(coord))
							valid = false;
					}

					if (valid) {

						ArrayList<Integer> vertices = new ArrayList<>();

						for (Couple<Integer, Integer> coord : coords) {
							vertices.add(correspondancesHexagons[coordsMatrixCoronenoid[coord.getY()][coord.getX()]]);
						}

						return vertices;
					}
				}
			}
		}

		return null;
	}

	private boolean isValid(Couple<Integer, Integer> coord) {

		int diameter = coordsMatrixCoronenoid.length;

        return coord.getX() >= 0 && coord.getX() < diameter && coord.getY() >= 0 && coord.getY() < diameter
                && coordsMatrixCoronenoid[coord.getY()][coord.getX()] != -1;
    }

	public static int rotation60(int diameter, int nbCrowns, int i) {
		return diameter * (nbCrowns - 1) - (i % diameter) * diameter + (i / diameter) * (diameter + 1);
	}
}
