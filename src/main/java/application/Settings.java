package application;

import javafx.stage.Screen;

import java.io.*;
import java.util.regex.Pattern;

public class Settings {
	
	/*
	 * Window's properties
	 */

	private boolean rememberSize;
	private double width;
	private double height;
	
	private boolean displayHomeWindow;
	
	/*
	 * Generation's properties
	 */

	private int generationTime;
	private String timeUnit;
	private int nbMaxSolutions;

	/*
	 * Getters and setters
	 */

	boolean remembersSize() {
		return rememberSize;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	void setHeight(double height) {
		this.height = height;
	}

	public void setRemembersSize(boolean rememberSize) {
		this.rememberSize = rememberSize;
	}

	public int getGenerationTime() {
		return generationTime;
	}

	public void setGenerationTime(int generationTime) {
		this.generationTime = generationTime;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getNbMaxSolutions() {
		return nbMaxSolutions;
	}

	public void setNbMaxSolutions(int nbMaxSolutions) {
		this.nbMaxSolutions = nbMaxSolutions;
	}

	boolean isDisplayHomeWindow() {
		return displayHomeWindow;
	}

	private void setDisplayHomeWindow(boolean displayHomeWindow) {
		this.displayHomeWindow = displayHomeWindow;
	}
	
	/*
	 * I/O methods
	 */

	static Settings readSettingsFile() throws IOException {

		Settings settings = new Settings();

		File file = new File("config");

		if (file.exists()) {

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			while ((line = reader.readLine()) != null) {

				String[] splittedLine = line.split(Pattern.quote(": "));

				double width;
				double height;
				boolean rememberSize;

				if ("remember-window-size".equals(splittedLine[0])) {
					rememberSize = Boolean.parseBoolean(splittedLine[1]);
					settings.setRemembersSize(rememberSize);
				}

				if ("width".equals(splittedLine[0])) {
					width = Double.parseDouble(splittedLine[1]);
					settings.setWidth(width);
				}

				else if ("height".equals(splittedLine[0])) {
					height = Double.parseDouble(splittedLine[1]);
					settings.setHeight(height);
				}

				else if ("generation-time-limit".equals(splittedLine[0])) {

					String timeProperties = splittedLine[1];
					String[] splittedTimeProperties = timeProperties.split(" ");

					settings.setGenerationTime(Integer.parseInt(splittedTimeProperties[0]));
					settings.setTimeUnit(splittedTimeProperties[1]);
				}

				else if ("generation-max-solutions".equals(splittedLine[0])) {
					settings.setNbMaxSolutions(Integer.parseInt(splittedLine[1]));
				}
				
				else if (splittedLine[0].contentEquals("home-window")) {
					settings.setDisplayHomeWindow(Boolean.parseBoolean(splittedLine[1]));
				}
			}

			reader.close();
		}

		else {

			double width = (Screen.getPrimary().getBounds().getWidth() * 2.0 / 2.5);
			double height = (Screen.getPrimary().getBounds().getHeight() * 2.0 / 2.5);

			settings.setWidth(width);
			settings.setHeight(height);

			settings.save();
		}

		return settings;
	}

	public void save() {

		File file = new File("config");

		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write("# Window's properties\n\n");

			writer.write("remember-window-size: " + rememberSize + "\n");
			writer.write("width: " + width + "\n");
			writer.write("height: " + height + "\n");

			writer.write("home-window: " + displayHomeWindow + "\n\n");
			
			writer.write("# Generation's properties\n\n");

			if (generationTime > 0 && timeUnit != null) {
				writer.write("generation-time-limit: " + generationTime + " " + timeUnit + "\n");
			}

			if (nbMaxSolutions > 0) {
				writer.write("generation-max-solutions: " + nbMaxSolutions + "\n\n");
			}

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
