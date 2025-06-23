package view.patterns;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public abstract class ListBox extends VBox {
    private PatternsEditionPane patternsEditionPane;
    private ListView<GridPane> listView;
    private String addLabel;    // the label of the add button
    private VBox patternListBox;
    private int selectedIndex;

    public ListBox (String addLabel, PatternsEditionPane patternsEditionPane) {
        super(5.0);
        this.addLabel = addLabel;
        HBox buttonBox = buildAddButtonBox();
        this.patternsEditionPane = patternsEditionPane;
        buildListView();

        this.getChildren().addAll(listView, buttonBox);
        this.setPrefHeight(1000);
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

    private Button buildAddButton() {
        Button addButton = new Button(addLabel);
        addButton.setPrefWidth(250);
        addButton.setOnAction(e -> addEntry());
        return addButton;
    }

    abstract void select(int index);

    abstract void addEntry();

    abstract void removeEntry(int index);

    ListView<GridPane> getListView () {
        return listView;
    }

    PatternsEditionPane getPatternsEditionPane () {
        return patternsEditionPane;
    }

    int getSelectedIndex () {
        return selectedIndex;
    }

    void setSelectedIndex (int index) {
        selectedIndex = index;
    }
}
