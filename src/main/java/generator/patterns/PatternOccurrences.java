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

	public PatternOccurrences() {
		occurrences = new ArrayList<>();
		coordinates = new ArrayList<>();
		allOutterHexagons = new ArrayList<>();
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
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

	private void addAllOccurrences(ArrayList<Integer[]> occurrences) {
		this.occurrences.addAll(occurrences);
	}

	private void addAllCoordinates(ArrayList<Couple<Integer, Integer>[]> coordinates) {
		this.coordinates.addAll(coordinates);
	}

	public void addAll(PatternOccurrences fragmentOccurrences) {
		System.out.println("Add all ");
		for (int i = 0; i < fragmentOccurrences.getOccurrences().size(); i++) {
			boolean found = false;
			for (int j = 0; (j < (occurrences.size()) && (!found)); j++) {
				found = (allPresentHexagons.get(j).equals(fragmentOccurrences.getAllPresentHexagons().get(i))) &&
						(allAbsentHexagons.get(j).equals(fragmentOccurrences.getAllAbsentHexagons().get(i))) &&
						(allOutterHexagons.get(j).equals(fragmentOccurrences.getAllOutterHexagons().get(i))) &&
						(allUnknownHexagons.get(j).equals(fragmentOccurrences.getAllUnknownHexagons().get(i)));
			}

			if (! found) {
				occurrences.add(fragmentOccurrences.getOccurrences().get(i));
				coordinates.add(fragmentOccurrences.getCoordinates().get(i));
				allOutterHexagons.add(fragmentOccurrences.getAllOutterHexagons().get(i));
				allPresentHexagons.add(fragmentOccurrences.getAllPresentHexagons().get(i));
				allAbsentHexagons.add(fragmentOccurrences.getAllAbsentHexagons().get(i));
				allUnknownHexagons.add(fragmentOccurrences.getAllUnknownHexagons().get(i));
			}
		}
	}

	public int size() {
		return occurrences.size();
	}

	public boolean occurrencesContains(Integer[] occurrence) {

		for (Integer[] occurrence2 : occurrences)
			if (occurrence.equals(occurrence2))
				return true;

		return false;
	}
}
