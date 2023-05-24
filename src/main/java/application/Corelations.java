package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Corelations extends Application {

	// int DATA_SIZE = 1000;
	// int data[] = new int[DATA_SIZE];
	private ArrayList<Double> data;
	private int[] group = new int[10];

	@Override
	public void start(Stage primaryStage) {

		prepareData();
		groupData();

		Label labelInfo = new Label();
		labelInfo.setText("java.version: " + System.getProperty("java.version") + "\n" + "javafx.runtime.version: "
				+ System.getProperty("javafx.runtime.version"));

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setCategoryGap(0);
		barChart.setBarGap(0);

		xAxis.setLabel("Range");
		yAxis.setLabel("Number of instances");

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("Histogram");
		series1.getData().add(new XYChart.Data("[0, 0.1]", group[0]));
		series1.getData().add(new XYChart.Data("]0.1, 0.2]", group[1]));
		series1.getData().add(new XYChart.Data("]0.2, 0.3]", group[2]));
		series1.getData().add(new XYChart.Data("]0.3, 0.4]", group[3]));
		series1.getData().add(new XYChart.Data("]0.4, 0.5]", group[4]));

		series1.getData().add(new XYChart.Data("]0.5, 0.6]", group[5]));
		series1.getData().add(new XYChart.Data("]0.6, 0.7]", group[6]));
		series1.getData().add(new XYChart.Data("]0.7, 0.8]", group[7]));
		series1.getData().add(new XYChart.Data("]0.8, 0.9]", group[8]));
		series1.getData().add(new XYChart.Data("]0.9, 1.0]", group[9]));

		barChart.getData().addAll(series1);

//        VBox vBox = new VBox();
//        vBox.getChildren().addAll(labelInfo, barChart);

		StackPane root = new StackPane();
		root.getChildren().add(barChart);

		Scene scene = new Scene(root, 800, 450);

		primaryStage.setTitle("plot");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// generate dummy random data
	private void prepareData() {

		data = new ArrayList<>();

		try {
			BufferedReader r = new BufferedReader(new FileReader(
					new File("/home/adrien/Documents/comparaisons_constraints/correlations/rectangles/values.txt")));
			String line;

			while ((line = r.readLine()) != null) {
				data.add(Double.parseDouble(line));
			}

			r.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// count data population in groups
	private void groupData() {

		group = new int[10];

		for (Double d : data) {

			if (d >= 0 && d <= 0.1)
				group[0]++;

			else if (d > 0.1 && d <= 0.2)
				group[1]++;

			else if (d > 0.2 && d <= 0.3)
				group[2]++;

			else if (d > 0.3 && d <= 0.4)
				group[3]++;

			else if (d > 0.4 && d <= 0.5)
				group[4]++;

			else if (d > 0.5 && d <= 0.6)
				group[5]++;

			else if (d > 0.6 && d <= 0.7)
				group[6]++;

			else if (d > 0.7 && d <= 0.8)
				group[7]++;

			else if (d > 0.8 && d <= 0.9)
				group[8]++;

			else if (d > 0.9 && d <= 1.0)
				group[9]++;
		}

	}
}
