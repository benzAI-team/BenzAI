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

public enum Post {
	;

	public static boolean isDatabaseConnected;

	@SuppressWarnings("rawtypes")
	public static List<Map> post(String urlString, String jsonInputString) throws IOException {

		System.out.println(jsonInputString);

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

			if (!"[]".contentEquals(response)) {

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

			else
				return new ArrayList<>();
		}
	}

	public static boolean checkDatabaseConnection() {

		isDatabaseConnected = true;

		try {
			String url = "https://benzenoids.lis-lab.fr/find_by_name/";
			String json = "{\"name\": \"1-11-20-27-28-29-30-39\"}";
			post(url, json);
		} catch (Exception e) {
			isDatabaseConnected = false;
			Utils.alert("Unable to connect to the database");
		}

		// System.out.println("Connection to database established");
		return isDatabaseConnected;
	}

	/*
	 * String url = "https://benzenoids.lis-lab.fr/find_name/"; String json =
	 * "{\"name\": \"1-11-20-27-28-29-30-39\"}"; post(url, json);
	 */
}
