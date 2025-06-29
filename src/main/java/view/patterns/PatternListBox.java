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

class PatternListBox extends VBox {
    private PatternsEditionPane patternsEditionPane;
    private ListView<GridPane> listView;
    private static int patternId = 0; // the nest pattern id
    private static ArrayList<GridPane> boxItems;
    private static ArrayList<PatternGroup> patternGroups = new ArrayList<>();
    private int selectedIndex;

    public PatternListBox (PatternsEditionPane patternsEditionPane) {
        super(5.0);
        Label titleLabel = new Label("Patterns");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox buttonBox = buildAddButtonBox();
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
                PatternCloseButton button = (PatternCloseButton) selection.getChildren().get(1);
                select(button.getIndex());
            }
        });
    }


    private HBox buildAddButtonBox() {
        Button addButton = buildAddButton();

        HBox buttonBox = new HBox(3.0);
        buttonBox.getChildren().add(addButton);
        return buttonBox;
    }

    public Button buildAddButton() {
        Button addButton = new Button("Add pattern");
        addButton.setPrefWidth(250);
        addButton.setOnAction(e -> {
            PatternGroup newPattern = new PatternGroup(patternsEditionPane, 3, null);
            addEntry(newPattern);
            patternsEditionPane.getPropertyListBox().addEntry(new PatternPropertyExistence(newPattern));
        });
        return addButton;
    }

    void select(int index) {
        patternsEditionPane.selectPatternGroup (patternGroups.get(index));
        selectedIndex = index;

        Label label = (Label) boxItems.get(index).getChildren().get(0);
        patternsEditionPane.getFieldName().setText(label.getText());
        if (patternsEditionPane.getBorderPane() != null) {
            patternsEditionPane.getBorderPane().setCenter(patternsEditionPane.getSelectedPatternGroup());
        }
    }

    static Label getNextLabel() {
        String new_label;
        Boolean find_new_label;
        do {
            patternId++;
            new_label = "Pattern_"+patternId;
            int i = 0;
            while ((i < patternGroups.size()) && (! patternGroups.get(i).getLabel().getText().equals(new_label))) {
                i++;
            }
            find_new_label = (i == patternGroups.size());
        }
        while (! find_new_label);

        return new Label(new_label);
    }

    void addEntry(PatternGroup newPattern) {
        Label label = newPattern.getLabel();

        PatternCloseButton button = new PatternCloseButton(patternsEditionPane, patternGroups.size());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        boxItems.add(pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        patternGroups.add(newPattern);

        listView.getSelectionModel().select(items.size()-1);
        select(patternGroups.size()-1);
    }


    void removeEntry(int index) {
        boxItems.remove(index);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        for (int i = index+1; i < patternGroups.size(); i++) {
            patternGroups.get(i).decrementIndex();
        }

        patternGroups.remove(index);

        if (index == selectedIndex)
            select(0);

        for (int i = 0; i < boxItems.size(); i++) {
            boxItems.get(i).getChildren().remove(1);
            boxItems.get(i).add(new PatternCloseButton(patternsEditionPane, i), 1, 0);
        }
    }

    ArrayList<PatternGroup> getPatternGroups () {
        return patternGroups;
    }

    static ArrayList<GridPane> getBoxItems() {
        return boxItems;
    }
}
