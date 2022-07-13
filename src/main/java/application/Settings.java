package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import javafx.stage.Screen;

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

	public boolean remembersSize() {
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

	public void setHeight(double height) {
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

	public boolean isRememberSize() {
		return rememberSize;
	}

	public void setRememberSize(boolean rememberSize) {
		this.rememberSize = rememberSize;
	}

	public boolean isDisplayHomeWindow() {
		return displayHomeWindow;
	}

	public void setDisplayHomeWindow(boolean displayHomeWindow) {
		this.displayHomeWindow = displayHomeWindow;
	}
	
	/*
	 * I/O methods
	 */

	public static Settings readConfigurationFile() throws IOException {

		Settings configuration = new Settings();

		File file = new File("config");

		if (file.exists()) {

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			while ((line = reader.readLine()) != null) {

				String[] splittedLine = line.split(Pattern.quote(": "));

				double width = -1.0;
				double height = -1.0;
				boolean rememberSize;

				if (splittedLine[0].equals("remember-window-size")) {
					rememberSize = Boolean.parseBoolean(splittedLine[1]);
					configuration.setRemembersSize(rememberSize);
				}

				if (splittedLine[0].equals("width")) {
					width = Double.parseDouble(splittedLine[1]);
					configuration.setWidth(width);
				}

				else if (splittedLine[0].equals("height")) {
					height = Double.parseDouble(splittedLine[1]);
					configuration.setHeight(height);
				}

				else if (splittedLine[0].equals("generation-time-limit")) {

					String timeProperties = splittedLine[1];
					String[] splittedTimeProperties = timeProperties.split(" ");

					configuration.setGenerationTime(Integer.parseInt(splittedTimeProperties[0]));
					configuration.setTimeUnit(splittedTimeProperties[1]);
				}

				else if (splittedLine[0].equals("generation-max-solutions")) {
					configuration.setNbMaxSolutions(Integer.parseInt(splittedLine[1]));
				}
				
				else if (splittedLine[0].contentEquals("home-window")) {
					configuration.setDisplayHomeWindow(Boolean.parseBoolean(splittedLine[1]));
				}
			}

			reader.close();
		}

		else {

			double width = (Screen.getPrimary().getBounds().getWidth() * 2.0 / 2.5);
			double height = (Screen.getPrimary().getBounds().getHeight() * 2.0 / 2.5);

			configuration.setWidth(width);
			configuration.setHeight(height);

			configuration.save();
		}

		return configuration;
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
