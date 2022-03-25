package view.groups;

import java.util.ArrayList;
import java.util.HashMap;

public class RBOResult {

	private HashMap<Double, ArrayList<Integer>> map;

	public RBOResult() {
		map = new HashMap<>();
	}

	public void add(Double value, Integer index) {

		if (map.get(value) != null)
			map.get(value).add(index);

		else {
			map.put(value, new ArrayList<>());
			map.get(value).add(index);
		}
	}

	public int size() {
		return map.size();
	}

	public HashMap<Double, ArrayList<Integer>> getMap() {
		return map;
	}
}
