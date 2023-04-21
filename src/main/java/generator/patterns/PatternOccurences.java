package generator.patterns;

import java.util.ArrayList;

import utils.Couple;

public class PatternOccurences {

	private final ArrayList<Integer[]> occurences;
	private final ArrayList<Couple<Integer, Integer>[]> coordinates;

	private final ArrayList<ArrayList<Integer>> allOutterHexagons;
	private final ArrayList<ArrayList<Integer>> allPresentHexagons;
	private final ArrayList<ArrayList<Integer>> allAbsentHexagons;
	private final ArrayList<ArrayList<Integer>> allUnknownHexagons;

	public PatternOccurences() {
		occurences = new ArrayList<>();
		coordinates = new ArrayList<>();
		allOutterHexagons = new ArrayList<>();
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
	}

	public ArrayList<Integer[]> getOccurences() {
		return occurences;
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

	public void addOccurence(Integer[] occurence) {
		occurences.add(occurence);
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

	private void addAllOccurences(ArrayList<Integer[]> occurences) {
		this.occurences.addAll(occurences);
	}

	private void addAllCoordinates(ArrayList<Couple<Integer, Integer>[]> coordinates) {
		this.coordinates.addAll(coordinates);
	}

	public void addAll(PatternOccurences fragmentOccurences) {
		this.addAllOccurences(fragmentOccurences.getOccurences());
		this.addAllCoordinates(fragmentOccurences.getCoordinates());
		allOutterHexagons.addAll(fragmentOccurences.getAllOutterHexagons());
		allPresentHexagons.addAll(fragmentOccurences.getAllPresentHexagons());
		allAbsentHexagons.addAll(fragmentOccurences.getAllAbsentHexagons());
		allUnknownHexagons.addAll(fragmentOccurences.getAllUnknownHexagons());

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
		return occurences.size();
	}

	public boolean occurencesContains(Integer[] occurence) {

		for (Integer[] occurence2 : occurences)
			if (occurence.equals(occurence2))
				return true;

		return false;
	}
}
