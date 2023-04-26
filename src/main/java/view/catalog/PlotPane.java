package view.catalog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

public abstract class PlotPane extends GridPane {

	private LineChart<Number, Number> lineChart;
	
	protected abstract void buildLineChart();
	
	public void exportAsPDF(File file) {
		//TODO linechart never assigned
		WritableImage wi = lineChart.snapshot(new SnapshotParameters(), new WritableImage(501, 408));
		BufferedImage awtImage = new BufferedImage((int)wi.getWidth(), (int)wi.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wi, awtImage), "png", file);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

