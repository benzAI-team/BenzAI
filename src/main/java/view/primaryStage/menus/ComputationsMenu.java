package view.primaryStage.menus;

import application.BenzenoidApplication;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import view.collections.BenzenoidCollectionsManagerPane;

public class ComputationsMenu {

	public ComputationsMenu() {
		// TODO Auto-generated constructor stub
	}

	public static Menu build(BenzenoidApplication app) {
		BenzenoidCollectionsManagerPane collectionsPane = (BenzenoidCollectionsManagerPane) app.getPanes()
				.getCollectionsPane();
		Menu computationsMenu = new Menu("C_omputations");

		MenuItem reItem = new MenuItem("Resonance energy (Lin)");
		MenuItem reLinFanItem = new MenuItem("Resonance energy (Lin & Fan)");
		MenuItem clarItem = new MenuItem("Clar cover");
		MenuItem clarREItem = new MenuItem("Clar aromaticity");
		MenuItem clarStatsItem = new MenuItem("Clar cover with fixed bond");
		MenuItem kekuleItem = new MenuItem("Kekulé structures");
		MenuItem rboItem = new MenuItem("Ring bond order");
		MenuItem irregularityStatsItem = new MenuItem("Irregularity statistics");
		MenuItem irSpectraItem = new MenuItem("IR spectra");
		MenuItem radicalarItem = new MenuItem("Radicalar statistics");
		MenuItem ims2d1aItem = new MenuItem("IMS2D-1A");

		computationsMenu.setOnShowing(e -> {
			app.switchMode(collectionsPane);
		});

		kekuleItem.setOnAction(e -> {
			collectionsPane.kekuleStructures();
		});

		reItem.setOnAction(e -> {
			collectionsPane.resonanceEnergyLin();
		});

		reLinFanItem.setOnAction(e -> {
			collectionsPane.resonanceEnergyLinFan();
		});

		clarItem.setOnAction(e -> {
			collectionsPane.clarCover();
		});

		clarREItem.setOnAction(e -> {
			collectionsPane.clarCoverRE();
		});

		clarStatsItem.setOnAction(e -> {
			collectionsPane.clarCoverStatsFixed();
			;
		});

		rboItem.setOnAction(e -> {
			collectionsPane.ringBoundOrder();
		});

		irregularityStatsItem.setOnAction(e -> {
			collectionsPane.irregularityStatistics();
		});

		irSpectraItem.setOnAction(e -> {
			collectionsPane.IRSpectra();
		});

		radicalarItem.setOnAction(e -> {
			collectionsPane.radicalarStatistics();
		});

		ims2d1aItem.setOnAction(e -> {
			collectionsPane.ims2d1a();
		});

		computationsMenu.getItems().addAll(reItem, reLinFanItem, clarItem, clarREItem, clarStatsItem, kekuleItem,
				rboItem, irregularityStatsItem, irSpectraItem, radicalarItem, ims2d1aItem);

		return computationsMenu;

	}
}
