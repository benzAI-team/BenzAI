package view.patterns;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

class PropertyListBox extends VBox {
    private PatternsEditionPane patternsEditionPane;
    private ListView<GridPane> listView;
    private static ArrayList<GridPane> boxItems;
    private static ArrayList<PatternProperty> patternProperties = new ArrayList<>();
    private int selectedIndex;

    public PropertyListBox(PatternsEditionPane patternsEditionPane) {
        super(5.0);
        Label titleLabel = new Label("Properties");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox buttonBox = new HBox(3.0);
        buttonBox.getChildren().addAll(buildAddButton(), buildModifyButton());
        this.patternsEditionPane = patternsEditionPane;
        buildListView();

        this.getChildren().addAll(titleLabel, listView, buttonBox);
        this.setPrefHeight(1000);

        boxItems = new ArrayList<>();
    }

    private void buildListView() {
        listView = new ListView<>();
        listView.setOnMouseClicked(event -> {
            GridPane selection = listView.getSelectionModel().getSelectedItem();
            if (selection != null) {
                PropertyCloseButton button = (PropertyCloseButton) selection.getChildren().get(1);
                select(button.getIndex());
            }
        });
    }

    public Button buildAddButton() {
        Button addButton = new Button("Add");
        addButton.setPrefWidth(125);
        addButton.setOnAction(e ->
        {
            Optional<PatternProperty> property = patternsEditionPane.getPropertyDialogBox(-1);
            property.ifPresent (value -> addEntry(value));
        });
        return addButton;
    }

    public Button buildModifyButton() {
        Button modifyButton = new Button("Modify");
        modifyButton.setPrefWidth(125);
        modifyButton.setOnAction(e ->
        {
            Optional<PatternProperty> property = patternsEditionPane.getPropertyDialogBox(selectedIndex);
            property.ifPresent (value -> modifyEntry(value));
        });
        return modifyButton;
    }

    void select(int index) {
        selectedIndex = index;
    }

    void addEntry(PatternProperty patternProperty) {
        Label label = new Label(patternProperty.getLabel());
        PropertyCloseButton button = new PropertyCloseButton(patternsEditionPane, patternProperties.size());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        patternProperties.add (patternProperty);

        boxItems.add(pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        listView.getSelectionModel().select(items.size()-1);
        select(patternProperties.size()-1);
    }

    void modifyEntry(PatternProperty patternProperty) {
        Label label = new Label(patternProperty.getLabel());
        PropertyCloseButton button = new PropertyCloseButton(patternsEditionPane, patternProperties.size());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        patternProperties.set(selectedIndex,patternProperty);

        boxItems.set(selectedIndex,pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);
    }

    void removeEntry(int index) {

    }

    public static ArrayList<PatternProperty> getPatternProperties() {
        return patternProperties;
    }
}