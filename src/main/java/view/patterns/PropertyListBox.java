package view.patterns;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

class PropertyListBox extends ListBox {
    private int patternTypeId; // the nest pattern type id
    private static ArrayList<GridPane> boxItems;
    private ArrayList<PatternType> patternTypes;

    public PropertyListBox(PatternsEditionPane patternsEditionPane) {
        super("Add property", patternsEditionPane);
        boxItems = new ArrayList<>();
        patternTypes = new ArrayList<>();
    }

    @Override
    public Button buildAddButton() {
        Button addButton = new Button(getAddLabel());
        addButton.setPrefWidth(250);
//        addButton.setOnAction(e -> addEntry(new PatternGroup(getPatternsEditionPane(), 3, null)));
        return addButton;
    }

    @Override
    void select(int index) {
        setSelectedIndex(index);
    }

    void addEntry(PatternType patternType) {
        Label label = new Label(patternType.getLabel());
        PropertyCloseButton button = new PropertyCloseButton(getPatternsEditionPane(), patternTypes.size());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        patternTypes.add (patternType);

        boxItems.add(pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        getListView().setItems(items);

        getListView().getSelectionModel().select(items.size()-1);
        select(patternTypes.size()-1);
    }

    @Override
    void removeEntry(int index) {

    }

    public ArrayList<PatternType> getPatternTypes() {
        return patternTypes;
    }
}