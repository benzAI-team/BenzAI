package view.primaryStage;

import application.BenzenoidApplication;
import javafx.scene.layout.Region;
import view.catalog.CatalogPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections_operations.CollectionsOperationsPane;
import view.draw.DrawBenzenoidPane;
import view.filtering.FilteringPane;
import view.generator.GeneratorPane;
import view.generator.preferences.GeneratorPreferencesPane;

public class Panes {
	private Region generatorPane;
	private Region collectionsPane;
	private DrawBenzenoidPane drawPane;
	private Region filteringPane;
	private Region databasePane;
	private Region operationPane;
	private Region generationPreferencesPane;

	public Panes(BenzenoidApplication app) {
		collectionsPane = new BenzenoidCollectionsManagerPane(app);
		generatorPane = new GeneratorPane(app);
		drawPane = new DrawBenzenoidPane(app, (BenzenoidCollectionsManagerPane) collectionsPane);
		databasePane = new CatalogPane(app);
		filteringPane = new FilteringPane(app, (BenzenoidCollectionsManagerPane) collectionsPane);
		operationPane = new CollectionsOperationsPane(app, (BenzenoidCollectionsManagerPane) collectionsPane);
		generationPreferencesPane = new GeneratorPreferencesPane(app);

	}

	public Region getGeneratorPane() {
		return generatorPane;
	}

	public void setGeneratorPane(Region generatorPane) {
		this.generatorPane = generatorPane;
	}

	public Region getCollectionsPane() {
		return collectionsPane;
	}

	public void setCollectionsPane(Region collectionsPane) {
		this.collectionsPane = collectionsPane;
	}

	public DrawBenzenoidPane getDrawPane() {
		return drawPane;
	}

	public void setDrawPane(DrawBenzenoidPane drawPane) {
		this.drawPane = drawPane;
	}

	public Region getFilteringPane() {
		return filteringPane;
	}

	public void setFilteringPane(Region filteringPane) {
		this.filteringPane = filteringPane;
	}

	public Region getDatabasePane() {
		return databasePane;
	}

	public void setDatabasePane(Region databasePane) {
		this.databasePane = databasePane;
	}

	public Region getOperationPane() {
		return operationPane;
	}

	public void setOperationPane(Region operationPane) {
		this.operationPane = operationPane;
	}

	public Region getGenerationPreferencesPane() {
		return generationPreferencesPane;
	}

	public void setGenerationPreferencesPane(Region generationPreferencesPane) {
		this.generationPreferencesPane = generationPreferencesPane;
	}
	
}
