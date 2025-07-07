package generator.patterns;

import utils.Couple;

import java.util.ArrayList;

public class PatternOccurrences {

	private final ArrayList<Integer[]> occurrences;
	private final ArrayList<Couple<Integer, Integer>[]> coordinates;

	private final ArrayList<ArrayList<Integer>> allOutterHexagons;
	private final ArrayList<ArrayList<Integer>> allPresentHexagons;
	private final ArrayList<ArrayList<Integer>> allAbsentHexagons;
	private final ArrayList<ArrayList<Integer>> allUnknownHexagons;

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

	public ArrayList<ArrayList<Integer>> getAllOutterHexagons() {
		return allOutterHexagons;
	}

	public ArrayList<ArrayList<Integer>> getAllPresentHexagons() {
		return allPresentHexagons;
	}

	public ArrayList<ArrayList<Integer>> getAllAbsentHexagons() {
		return allAbsentHexagons;
	}

	public ArrayList<ArrayList<Integer>> getAllUnknownHexagons() {
		return allUnknownHexagons;
	}

	public void addOccurrence(Integer[] occurrence) {
		occurrences.add(occurrence);
	}

	public void addCoordinate(Couple<Integer, Integer>[] coordinate) {
		coordinates.add(coordinate);
	}

	public void addOutterHexagons(ArrayList<Integer> hexagons) {
		allOutterHexagons.add(hexagons);
	}

	public void addPresentHexagons(ArrayList<Integer> hexagons) {
		allPresentHexagons.add(hexagons);
	}

	public void addAbsentHexagons(ArrayList<Integer> hexagons) {
		allAbsentHexagons.add(hexagons);
	}

	public void addUnknownHexagons(ArrayList<Integer> hexagons) {
		allUnknownHexagons.add(hexagons);
	}

	private void addAllOccurrences(ArrayList<Integer[]> occurrences) {
		this.occurrences.addAll(occurrences);
	}

	private void addAllCoordinates(ArrayList<Couple<Integer, Integer>[]> coordinates) {
		this.coordinates.addAll(coordinates);
	}

	public void addAll(PatternOccurrences fragmentOccurrences) {
		this.addAllOccurrences(fragmentOccurrences.getOccurrences());
		this.addAllCoordinates(fragmentOccurrences.getCoordinates());
		allOutterHexagons.addAll(fragmentOccurrences.getAllOutterHexagons());
		allPresentHexagons.addAll(fragmentOccurrences.getAllPresentHexagons());
		allAbsentHexagons.addAll(fragmentOccurrences.getAllAbsentHexagons());
		allUnknownHexagons.addAll(fragmentOccurrences.getAllUnknownHexagons());

//		for (int i = 0; i < fragmentOccurences.size(); i++) {
//			Integer[] occurence = fragmentOccurences.getOccurences().get(i);
//			Couple<Integer, Integer>[] coord = fragmentOccurences.getCoordinates().get(i);
//
//			if (occurencesContains(occurence)) {
//				occurences.add(occurence);
//				coordinates.add(coord);
//
//				if (i < fragmentOccurences.getAllOutterHexagons().size())
//					allOutterHexagons.add(fragmentOccurences.getAllOutterHexagons().get(i));
//
//				if (i < fragmentOccurences.getAllPresentHexagons().size())
//					allPresentHexagons.add(fragmentOccurences.getAllPresentHexagons().get(i));
//
//				if (i < fragmentOccurences.getAllAbsentHexagons().size())
//					allAbsentHexagons.add(fragmentOccurences.getAllAbsentHexagons().get(i));
//
//				if (i < fragmentOccurences.getAllUnknownHexagons().size())
//					allUnknownHexagons.add(fragmentOccurences.getAllUnknownHexagons().get(i));
//			}
//
//		}
	}

	public int size() {
		return occurrences.size();
	}

	public boolean occurencesContains(Integer[] occurence) {

		for (Integer[] occurence2 : occurrences)
			if (occurence.equals(occurence2))
				return true;

		return false;
	}
}
