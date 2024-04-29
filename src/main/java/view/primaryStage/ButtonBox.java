package view.primaryStage;

import application.Operation;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import utils.Utils;

public class ButtonBox extends HBox {
    private final String name;
    private final ScrollPaneWithPropertyList embeddingPane;
    private Button addButton;
    private Button closeButton;
    private Button operationButton;
    private Button stopButton;

    public ButtonBox(String name, ScrollPaneWithPropertyList embeddingPane){
        super(5.0);
        this.name = name;
        this.embeddingPane = embeddingPane;
        buildButtons();
    }

    public void buildButtons() {
        addButton = buildAddButton();
        closeButton = buildCloseButton();
        stopButton = buildStopButton(embeddingPane.getOperation());
        operationButton = buildOperationButton(embeddingPane.getOperation());
        getChildren().addAll(closeButton, addButton, operationButton);
    }

    protected Button buildAddButton() {
        Button addButton = new Button();
        ImageView imageAdd = new ImageView(new Image("/resources/graphics/icon-add.png"));
        addButton.setGraphic(imageAdd);
        Tooltip.install(addButton, new Tooltip("Add new criterion"));
        addButton.resize(30, 30);
        addButton.setStyle("-fx-background-color: transparent;");
        addButton.setOnAction(e -> {
            if (embeddingPane.containsInvalidCriterion()) {
                Utils.alert("Invalid criterion(s)");
            } else {
                embeddingPane.addEmptyCriterionBox();
                //System.out.println(getNbBoxCriterions() + " criterions");
                embeddingPane.placeComponents();
            }
        });
        return addButton;
    }

    protected Button buildCloseButton() {
        Button closeButton = new Button();
        ImageView imageClose = new ImageView(new Image("/resources/graphics/icon-close.png"));
        closeButton.setGraphic(imageClose);
        Tooltip.install(closeButton, new Tooltip("Return to the collection"));
        closeButton.resize(30, 30);
        closeButton.setStyle("-fx-background-color: transparent;");

        closeButton.setOnAction(e -> embeddingPane.getApplication().switchMode(embeddingPane.getApplication().getPanes().getCollectionsPane()));
        return closeButton;
    }

    protected Button buildStopButton(Operation operation) {
        ImageView imageStop = new ImageView(new Image("/resources/graphics/icon-stop.png"));
        Button stopButton = new Button();
        stopButton.setGraphic(imageStop);
        Tooltip.install(stopButton, new Tooltip("Stop " + name));
        stopButton.resize(30, 30);
        stopButton.setStyle("-fx-background-color: transparent;");
        stopButton.setOnAction(e -> {
            if(operation.isPossible())
                operation.stop(embeddingPane);
        });
        return stopButton;
    }


    protected Button buildOperationButton(Operation operation) {
        Button operationButton = new Button();
        ImageView imageGenerate = new ImageView(new Image("/resources/graphics/icon-resume.png"));
        operationButton.setGraphic(imageGenerate);
        Tooltip.install(operationButton, new Tooltip(name + " benzenoids"));
        operationButton.setStyle("-fx-background-color: transparent;");
        operationButton.resize(30, 30);
        operationButton.setOnAction(e -> {
            if(operation.isPossible())
                operation.run(embeddingPane);
        });
        return operationButton;
    }

    public String getName() {
        return name;
    }

    public ScrollPaneWithPropertyList getEmbeddingPane() {
        return embeddingPane;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Button getOperationButton() {
        return operationButton;
    }

    public Button getStopButton() {
        return stopButton;
    }
}
