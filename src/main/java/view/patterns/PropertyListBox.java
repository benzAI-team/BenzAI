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
    private static ArrayList<PatternProperty> properties = new ArrayList<>();
    private int selectedIndex;
    private int positivePropertyNumber;

    public PropertyListBox(PatternsEditionPane patternsEditionPane) {
        super(5.0);
        positivePropertyNumber = 0;
        Label titleLabel = new Label("Properties");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox buttonBox = new HBox(3.0);
        buttonBox.getChildren().addAll(buildModifyButton());
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

            if (event.getClickCount() == 2) {
                Optional<PatternProperty> property;
                property = patternsEditionPane.getPropertyDialogBox(selectedIndex);
                property.ifPresent (value -> modifyEntry(value));
            }
        });
    }

    public Button buildModifyButton() {
        Button modifyButton = new Button("Modify");
        modifyButton.setPrefWidth(250);
        modifyButton.setOnAction(e -> {
            Optional<PatternProperty> property;
            property = patternsEditionPane.getPropertyDialogBox(selectedIndex);
            property.ifPresent (value -> modifyEntry(value));
        });
        return modifyButton;
    }

    void select(int index) {
        selectedIndex = index;
    }

    void addEntry(PatternProperty patternProperty) {
        Label label = new Label(patternProperty.getLabel());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        PropertyCloseButton button = new PropertyCloseButton(patternsEditionPane, properties.size());

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        properties.add (patternProperty);

        boxItems.add(pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        listView.getSelectionModel().select(items.size()-1);
        select(properties.size()-1);

        if (patternProperty.getType() != 1) {
            positivePropertyNumber++;
        }

        patternsEditionPane.getInteractionListBox().getAddButton().setDisable(positivePropertyNumber < 2);
    }

    void modifyEntry(PatternProperty patternProperty) {
        PatternProperty property = properties.get(selectedIndex);
        if (property.getType() != 1) {
            positivePropertyNumber--;
        }

        Label label = new Label(patternProperty.getLabel());
        PropertyCloseButton button = new PropertyCloseButton(patternsEditionPane, selectedIndex);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        properties.set(selectedIndex,patternProperty);

        boxItems.set(selectedIndex,pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        listView.getSelectionModel().select(selectedIndex);
        select(selectedIndex);

        if (patternProperty.getType() != 1) {
            positivePropertyNumber++;
        }

        patternsEditionPane.getInteractionListBox().getAddButton().setDisable(positivePropertyNumber < 2);
    }

    void removeEntry(int index) {
        PatternProperty property = properties.get(index);
        if (property.getType() != 1) {
            positivePropertyNumber--;
            patternsEditionPane.getInteractionListBox().getAddButton().setDisable(positivePropertyNumber < 2);

            // we remove the interaction involving the pattern (if any)
            patternsEditionPane.getInteractionListBox().remove(property);
        }

        boxItems.remove(index);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        properties.remove(index);

        select(0);

        for (int i = 0; i < boxItems.size(); i++) {
            boxItems.get(i).getChildren().remove(1);
            boxItems.get(i).add(new PropertyCloseButton(patternsEditionPane, i), 1, 0);
        }
    }


    public static ArrayList<PatternProperty> getPatternProperties() {
        return properties;
    }

    public static ArrayList<GridPane> getBoxItems() {
        return boxItems;
    }
}