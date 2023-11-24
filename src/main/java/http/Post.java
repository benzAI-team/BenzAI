package http;

import com.google.gson.Gson;
import utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Post {

	public static boolean isDatabaseConnected;
  private static String databaseServername = "https://benzenoids.lis-lab.fr";

	@SuppressWarnings("rawtypes")
	public static List<Map> post(String service, String jsonInputString) throws IOException {

    String urlString = databaseServername + "/" + service;

		System.out.println("Ma requete "+jsonInputString+" @ "+urlString);

		URL url = new URL(urlString);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");

		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
			StringBuilder response = new StringBuilder();
			String responseLine;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}

			// System.out.println(response.toString());

			if ("[]".contentEquals(response))
				return new ArrayList<>();
			else {

				String res = response.toString();
				res = res.substring(2, res.length() - 2);

				String[] results = res.split(Pattern.quote("},{"));

				List<Map> maps = new ArrayList<>();

				for (int i = 0; i < results.length; i++) {
					results[i] = "{" + results[i] + "}";
					Gson gson = new Gson();
					Map map = gson.fromJson(results[i], Map.class);
					maps.add(map);
				}

				br.close();
				con.disconnect();

				return maps;

			}
		}
	}

	public static boolean checkDatabaseConnection() {

		isDatabaseConnected = true;

		try {
			String service = "find_benzenoids/";
			String json = "{\"label\": \"= 1-11-20-27-28-29-30-39\"}";
			post(service, json);
		} catch (Exception e) {
			isDatabaseConnected = false;
			Utils.alert("Unable to connect to the database");
		}

		// System.out.println("Connection to database established");
		return isDatabaseConnected;
	}
}
