package generator;

import java.util.ArrayList;
import java.util.Collections;

import molecules.Node;

public class Solution {

	private ArrayList<Integer> vertices;
	private int[] correspondancesHexagons; //avec variables hors coro -> sans
	private int [] hexagonsCorrespondances;//sans variables hors coro -> avec
	private int[][] coordsMatrixCoronenoid;
	private int coronenoidCenter;
	private Node[] coronenoidNodes;
	private int nbCrowns;
	
	
	public Solution(Node[] coronenoidNodes, int[] correspondancesHexagons, int [] hexagonsCorrespondances, int[][] coordsMatrixCoronenoid,
			int coronenoidCenter, int nbCrowns, ArrayList<Integer> vertices) {
		this.coronenoidNodes = coronenoidNodes;
		this.correspondancesHexagons = correspondancesHexagons;
		this.hexagonsCorrespondances = hexagonsCorrespondances;
		this.coordsMatrixCoronenoid = coordsMatrixCoronenoid;
		this.coronenoidCenter = coronenoidCenter;
		this.nbCrowns = nbCrowns;
		this.vertices = vertices;
	}

	
	
	public ArrayList<Integer> getVertices() {
		return vertices;
	}

	public int getVertex(int index) {
		return vertices.get(index);
	}

	public Node[] getCoronenoidNodes() {
		return coronenoidNodes;
	}

	public Node getCoronenoidNode(int index) {
		return coronenoidNodes[index];
	}

	public int getNbNodes() {
		return vertices.size();
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

			for (int i = 0; i < vertices.size(); i++) {

				int vertexIndex1 = vertices.get(i);
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

				for (int i = 0; i < vertices.size(); i++) {

					int vertexIndex1 = vertices.get(i);
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
	
	public ArrayList<ArrayList<Integer>> allTranslations() {
		
		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();
		int diameter = coordsMatrixCoronenoid.length;
		
		ArrayList<ArrayList<Integer>> rotations = allRotations();
		
		for (ArrayList<Integer> rotation : rotations) {
			
			for (int xShift = - diameter ; xShift <= diameter ; xShift ++) {
				for (int yShift = - diameter ; yShift <= diameter ; yShift ++) {
					
					ArrayList<Integer> translation = new ArrayList<>();
					boolean embedded = true;
					
					for (int i = 0 ; i < rotation.size() ; i++) {
					
						int vertexIndex = rotation.get(i);

						Node node = coronenoidNodes[vertexIndex];

						int x = node.getX() + xShift;
						int y = node.getY() + yShift;

						if (!(x >= 0 && x < diameter && y >= 0 && y < diameter)
								|| coordsMatrixCoronenoid[x][y] == -1) {
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
		
		int diameter = coordsMatrixCoronenoid.length;
		
		
		ArrayList<Integer> initialRotation = new ArrayList<>();
		for (Integer vertex : vertices)
			initialRotation.add(hexagonsCorrespondances[vertex]);
		
		rotations.add(initialRotation);
		translatedRotations.add(vertices);
		
		for (int i = 1 ; i < 6 ; i ++) {
			
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
	
	public static int rotation60(int diameter, int nbCrowns, int i) {
		return diameter * (nbCrowns - 1 ) - (i % diameter) * diameter + (i / diameter) * (diameter + 1); 
	}
}
