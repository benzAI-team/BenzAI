package view.catalog;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class BenzenoidPane extends GridPane{

	private CatalogPane catalogPane;
	
	private int nbCrowns;
	private String solution;
	private Group benzenoidDraw;
	private String description;
	
	private boolean isSelected;
	
	private ArrayList<Integer> verticesSolution;
	
	public BenzenoidPane(CatalogPane catalogPane, int nbCrowns, String solution, Group benzenoidDraw, String description, ArrayList<Integer> verticesSolution) {
		
		super();
		
		this.catalogPane = catalogPane;
		
		this.nbCrowns = nbCrowns;
		this.solution = solution;
		this.benzenoidDraw = benzenoidDraw;
		this.description = description;
		
		isSelected = false;
		
		this.setStyle("-fx-border-color: black;" + "-fx-border-width: 4;" + "-fx-border-radius: 10px;");
		
		addItems();
		
		this.verticesSolution = verticesSolution;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addItems() {
		
		this.setPadding(new Insets(5));
		this.setHgap(5);
		this.setVgap(5);
		
		//HBox hBox = new HBox
		
		this.add(benzenoidDraw, 0, 0, 2, 2);
		this.add(new Label(description), 2, 0, 2, 2);
	    
	    this.setOnMouseClicked(new EventHandler() {

			@Override
			public void handle(Event arg0) {
				
				if (!isSelected) 
					select();
				
				else
					unselect();
				
			}
	    	
	    });
	    
		this.setMinSize(300, 200);
		
	}
	
	private void unselect() {
		/*
		isSelected = false;
		parameterPane.setSelectedBenzenoidPane(null);
		this.setStyle("-fx-border-color: black;" + "-fx-border-width: 4;" + "-fx-border-radius: 10px;");
		*/
	}
	
	private void select() {
		/*
		if (parameterPane.getSelectedBenzenoidPane() != null)
			parameterPane.getSelectedBenzenoidPane().unselect();
		
		isSelected = true;
		
		parameterPane.setSelectedBenzenoidPane(this);
		setStyle("-fx-border-color: blue;" + "-fx-border-width: 4;" + "-fx-border-radius: 10px;");
		*/
	}
}
