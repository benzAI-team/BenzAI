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
import java.io.IOException;

public class Panes {
	private final Region generatorPane;
	private final Region collectionsPane;
	private final DrawBenzenoidPane drawPane;
	private final Region filteringPane;
	private Region databasePane;
	private final Region operationPane;
	private final Region generationPreferencesPane;

	public Panes(BenzenoidApplication app) throws Exception, IOException {
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

	public Region getCollectionsPane() {
		return collectionsPane;
	}

	public DrawBenzenoidPane getDrawPane() {
		return drawPane;
	}

	public Region getFilteringPane() {
		return filteringPane;
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

	public Region getGenerationPreferencesPane() {
		return generationPreferencesPane;
	}

}
