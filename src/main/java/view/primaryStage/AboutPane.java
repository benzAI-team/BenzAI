package view.primaryStage;

import application.BenzenoidApplication;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class AboutPane extends BorderPane {

	private BenzenoidApplication application;

	public AboutPane(BenzenoidApplication application) {
		super();
		this.application = application;
		initialize();
	}

	private void initialize() {

		ImageView logo = new ImageView(new Image("resources/graphics/benzAI-image.png"));
		logo.resize(277, 336);
		logo.setFitHeight(277);
		logo.setFitWidth(336);

		Label benzaiLabel = new Label("BenzAI ");
		Label softwareLabel = new Label("Software");

		benzaiLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 45));
		softwareLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, 45));

		HBox boxTitle = new HBox(3.0);
		boxTitle.getChildren().addAll(benzaiLabel, softwareLabel);

		Label versionLabel = new Label("Version 1.0 for Linux");

		Hyperlink releases = new Hyperlink("releases");

		releases.setOnAction(e -> {
			application.getHostServices().showDocument("https://github.com/benzAI-team/BenzAI/releases");
		});

		HBox boxVersion = new HBox(3.0);
		boxVersion.getChildren().addAll(versionLabel, releases);

		Label descriptionLabel = new Label(
				"BenzAI is an open-source software for chemists that addresses\nseveral questions about benzenoids using artificial intelligence\n techniques.");

		Hyperlink website = new Hyperlink("Visit our website");

		website.setOnAction(e -> {
			application.getHostServices().showDocument("https://benzai-team.github.io/");
		});

		Label teamLabel = new Label("Developed by benzAI-team ");

		HBox teamBox = new HBox(3.0);
		teamBox.getChildren().addAll(teamLabel, website);

		VBox vBox = new VBox(14.0);
		vBox.getChildren().addAll(boxTitle, boxVersion, new HBox(descriptionLabel), teamBox);

		this.setLeft(logo);
		this.setCenter(vBox);

		BorderPane.setMargin(logo, new Insets(15, 15, 15, 15));
		BorderPane.setMargin(vBox, new Insets(15, 15, 15, 15));
	}

}
