package http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Test {

	public static void main(String [] args) throws IOException {
		String json = "{\"idCriterion\": \"\", \"nameCriterion\": \"\", \"nbHexagonsCriterion\": \"= 5\", \"nbCarbonsCriterion\": \"\", \"nbHydrogensCriterion\": \"\", \"irregularityCriterion\": \"\"}";
		List<Map> results = Post.post("https://benzenoids.lis-lab.fr/find_nics/", json);
		for (Map m : results)
			System.out.println(m.toString());
	}
}
