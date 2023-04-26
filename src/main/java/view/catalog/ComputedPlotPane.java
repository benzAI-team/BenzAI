package view.catalog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import spectrums.ResultSpectrums;

public class ComputedPlotPane extends PlotPane{

	private final ResultSpectrums result;
	private LineChart<Number, Number> lineChart;
	
	public ComputedPlotPane(ResultSpectrums result) {
		super();
		this.result = result;
		buildLineChart();
		this.add(lineChart,0,0);
	}
	
	public ResultSpectrums getResult() {
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void buildLineChart() {
	
		NumberAxis xAxis = new NumberAxis();
	    NumberAxis yAxis = new NumberAxis();
	    
	    xAxis.setLabel("V");
	    yAxis.setLabel("Intensity");
	    
	    xAxis.setLowerBound(result.getParameter().getVMin());
	    xAxis.setUpperBound(result.getParameter().getVMax());
	    xAxis.setAutoRanging(false);
	    
	    xAxis.setAnimated(false); 
	    yAxis.setAnimated(false);
	    
	    lineChart = new LineChart<>(xAxis, yAxis);
	    lineChart.setTitle(result.getPAHClass().getTitle());
	    
	    XYChart.Series series = new XYChart.Series();
        series.setName("Intensities");
	    
	    int V = result.getParameter().getVMin();
	    for (int index = 0 ; index < result.getEnergies().size() ; index ++) {
	    	
	    	double energy = result.getEnergies().get(index);
	    	series.getData().add(new XYChart.Data(V, energy));
	    	
	    	V += result.getParameter().getStep();
	    }
	    
	    lineChart.getData().add(series);
	}
	
	public void exportAsPDF(File file) {
		
		WritableImage wi = lineChart.snapshot(new SnapshotParameters(), new WritableImage(501, 408));
		BufferedImage awtImage = new BufferedImage((int)wi.getWidth(), (int)wi.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wi, awtImage), "png", file);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
