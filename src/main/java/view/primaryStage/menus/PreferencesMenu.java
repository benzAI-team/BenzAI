package view.primaryStage.menus;

import application.BenzenoidApplication;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import solveur.Aromaticity.RIType;
import view.collections.BenzenoidsCollectionsManagerPane;
import view.collections.IRSpectraParameterPane;
import view.groups.AromaticityDisplayType;
import view.groups.AromaticityGroup;

public class PreferencesMenu {

	public PreferencesMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {
		BenzenoidsCollectionsManagerPane collectionsPane =  (BenzenoidsCollectionsManagerPane) app.getPanes().getCollectionsPane();
		
		Menu preferencesMenu = new Menu("P_references");
		Menu aromaticityDisplayMenu = new Menu("Resonance energy display");
		CheckMenuItem localColorScaleItem = new CheckMenuItem("Local color scale");
		CheckMenuItem globalColorScaleItem = new CheckMenuItem("Global color scale");

		Menu riMenu = new Menu("Ri values");
		CheckMenuItem optimizedValues = new CheckMenuItem("Optimized values");
		CheckMenuItem defaultValues = new CheckMenuItem("Default values");
		riMenu.getItems().addAll(optimizedValues, defaultValues);

		MenuItem irSpectraParameterItem = new MenuItem("IR Spectra parameter");

		Menu windowMenu = new Menu("Window");

		localColorScaleItem.setSelected(true);
		AromaticityGroup.aromaticityDisplayType = AromaticityDisplayType.LOCAL_COLOR_SCALE;

		preferencesMenu.setOnShowing(e -> {
			app.switchMode(collectionsPane);
		});

		localColorScaleItem.setOnAction(e -> {

			if (localColorScaleItem.isSelected()) {
				globalColorScaleItem.setSelected(false);
				AromaticityGroup.aromaticityDisplayType = AromaticityDisplayType.LOCAL_COLOR_SCALE;
				collectionsPane.refreshColorScales();
			}

			else
				localColorScaleItem.setSelected(true);

		});

		globalColorScaleItem.setOnAction(e -> {

			if (globalColorScaleItem.isSelected()) {
				localColorScaleItem.setSelected(false);
				AromaticityGroup.aromaticityDisplayType = AromaticityDisplayType.GLOBAL_COLOR_SCALE;
				collectionsPane.refreshColorScales();
			}

			else
				globalColorScaleItem.setSelected(true);

		});

		optimizedValues.setSelected(true);

		optimizedValues.setOnAction(e -> {
			defaultValues.setSelected(false);
			collectionsPane.refreshRIType(RIType.OPTIMIZED);
		});

		defaultValues.setOnAction(e -> {
			optimizedValues.setSelected(false);
			collectionsPane.refreshRIType(RIType.NORMAL);
		});

		irSpectraParameterItem.setOnAction(e -> {
			Region parameterPane = new IRSpectraParameterPane(collectionsPane);
			Stage stage = new Stage();
			stage.setTitle("Set parameters");

			Scene scene = new Scene(parameterPane, 573, 535);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		});

		MenuItem generationPreferencesItem = new MenuItem("Generation preferences");
		generationPreferencesItem.setOnAction(e -> {
			app.switchMode(app.getPanes().getGenerationPreferencesPane());
		});

		aromaticityDisplayMenu.getItems().addAll(localColorScaleItem, globalColorScaleItem);
		preferencesMenu.getItems().addAll(aromaticityDisplayMenu, riMenu, irSpectraParameterItem, windowMenu,
				generationPreferencesItem);

		CheckMenuItem rememberResizeItem = new CheckMenuItem("Remember windows size");

		rememberResizeItem.setOnAction(e -> {

			if (rememberResizeItem.isSelected())
				app.getConfiguration().setRemembersSize(true);

			else
				app.getConfiguration().setRemembersSize(false);

			app.getConfiguration().save();
		});

		windowMenu.getItems().add(rememberResizeItem);

		return preferencesMenu;

	}
}
