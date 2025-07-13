package generator.patterns;

import utils.Couple;

import java.util.ArrayList;
import java.util.HashSet;

public class PatternOccurrences {

	private final ArrayList<Integer[]> occurrences;
	private final ArrayList<Couple<Integer, Integer>[]> coordinates;
	private final ArrayList<HashSet<Integer>> allOutterHexagons;
	private final ArrayList<HashSet<Integer>> allPresentHexagons;
	private final ArrayList<HashSet<Integer>> allAbsentHexagons;
	private final ArrayList<HashSet<Integer>> allUnknownHexagons;
	private final ArrayList<HashSet<Integer>> allEdgeHexagons;
	private final ArrayList<ArrayList<Couple<Integer,Integer>>> allEdgeCoords;
	private final ArrayList<HashSet<Integer>> allEdgePositiveNeighborHexagons;

	public PatternOccurrences() {
		occurrences = new ArrayList<>();
		coordinates = new ArrayList<>();
		allOutterHexagons = new ArrayList<>();
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
		allEdgeHexagons = new ArrayList<>();
		allEdgePositiveNeighborHexagons = new ArrayList<>();
		allEdgeCoords = new ArrayList<>();
	}

	public ArrayList<Integer[]> getOccurrences() {
		return occurrences;
	}

	public ArrayList<Couple<Integer, Integer>[]> getCoordinates() {
		return coordinates;
	}

	public ArrayList<HashSet<Integer>> getAllOutterHexagons() {
		return allOutterHexagons;
	}

	public ArrayList<HashSet<Integer>> getAllPresentHexagons() {
		return allPresentHexagons;
	}

	public ArrayList<HashSet<Integer>> getAllAbsentHexagons() {
		return allAbsentHexagons;
	}

	public ArrayList<HashSet<Integer>> getAllUnknownHexagons() {
		return allUnknownHexagons;
	}

	public ArrayList<HashSet<Integer>> getAllEdgeHexagons() {
		return allEdgeHexagons;
	}

	public ArrayList<ArrayList<Couple<Integer, Integer>>> getAllEdgeCoords() {
		return allEdgeCoords;
	}

	public ArrayList<HashSet<Integer>> getAllEdgePositiveNeighborHexagons() {
		return allEdgePositiveNeighborHexagons;
	}

	public void addOccurrence(Integer[] occurrence) {
		occurrences.add(occurrence);
	}

	public void addCoordinate(Couple<Integer, Integer>[] coordinate) {
		coordinates.add(coordinate);
	}

	public void addOutterHexagons(ArrayList<Integer> hexagons) {
		allOutterHexagons.add(new HashSet<>(hexagons));
	}

	public void addPresentHexagons(ArrayList<Integer> hexagons) {
		allPresentHexagons.add(new HashSet<>(hexagons));
	}

	public void addAbsentHexagons(ArrayList<Integer> hexagons) {
		allAbsentHexagons.add(new HashSet<>(hexagons));
	}

	public void addUnknownHexagons(ArrayList<Integer> hexagons) {
		allUnknownHexagons.add(new HashSet<>(hexagons));
	}

	public void addEdgeHexagons(ArrayList<Integer> hexagons) {
		allEdgeHexagons.add(new HashSet<>(hexagons));
	}

	public void addEdgeCoords(ArrayList<Couple<Integer,Integer>> coords) {
		allEdgeCoords.add(coords);
	}

	public void addEdgePositiveNeighborHexagons(ArrayList<Integer> hexagons) {
		allEdgePositiveNeighborHexagons.add(new HashSet<>(hexagons));
	}

	public void addAll(PatternOccurrences fragmentOccurrences) {
		System.out.println("Add all ");
		for (int i = 0; i < fragmentOccurrences.getOccurrences().size(); i++) {
			boolean found = false;
			for (int j = 0; (j < (occurrences.size()) && (!found)); j++) {
				found = (allPresentHexagons.get(j).equals(fragmentOccurrences.getAllPresentHexagons().get(i))) &&
						(allAbsentHexagons.get(j).equals(fragmentOccurrences.getAllAbsentHexagons().get(i))) &&
						(allOutterHexagons.get(j).equals(fragmentOccurrences.getAllOutterHexagons().get(i))) &&
						(allUnknownHexagons.get(j).equals(fragmentOccurrences.getAllUnknownHexagons().get(i))) &&
						(allEdgeHexagons.get(j).equals(fragmentOccurrences.getAllEdgeHexagons().get(i)));
			}

			if (! found) {
				occurrences.add(fragmentOccurrences.getOccurrences().get(i));
				coordinates.add(fragmentOccurrences.getCoordinates().get(i));
				allOutterHexagons.add(fragmentOccurrences.getAllOutterHexagons().get(i));
				allPresentHexagons.add(fragmentOccurrences.getAllPresentHexagons().get(i));
				allAbsentHexagons.add(fragmentOccurrences.getAllAbsentHexagons().get(i));
				allUnknownHexagons.add(fragmentOccurrences.getAllUnknownHexagons().get(i));
				allEdgeHexagons.add(fragmentOccurrences.getAllEdgeHexagons().get(i));
				allEdgeCoords.add(fragmentOccurrences.getAllEdgeCoords().get(i));
				allEdgePositiveNeighborHexagons.add(fragmentOccurrences.getAllEdgePositiveNeighborHexagons().get(i));
			}
		}
	}

	public int size() {
		return occurrences.size();
	}
}
