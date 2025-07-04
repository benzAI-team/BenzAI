package view.patterns;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

class PatternListBox extends ListBox {
    private static int patternId; // the nest pattern id
    private static ArrayList<GridPane> boxItems;
    private static ArrayList<PatternGroup> patternGroups = new ArrayList<>();
    private int selectedIndex;

    public PatternListBox (PatternsEditionPane patternsEditionPane) {
        super("Add pattern", patternsEditionPane);
        boxItems = new ArrayList<>();
        addEntry();

    }

    @Override
    void select(int index) {
        getPatternsEditionPane().selectPatternGroup (patternGroups.get(index));
        setSelectedIndex(index);

        Label label = (Label) boxItems.get(index).getChildren().get(0);
        getPatternsEditionPane().getFieldName().setText(label.getText());
        if (getPatternsEditionPane().getBorderPane() != null) {
            getPatternsEditionPane().getBorderPane().setCenter(getPatternsEditionPane().getSelectedPatternGroup());
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

    @Override
    void addEntry() {
        Label label = getNextLabel();

        PatternCloseButton button = new PatternCloseButton(getPatternsEditionPane(), patternGroups.size());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        boxItems.add(pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        getListView().setItems(items);

        PatternGroup patternGroup = new PatternGroup(getPatternsEditionPane(), 3, patternGroups.size(), label);
        patternGroups.add(patternGroup);

        if (patternGroups.size() > 1)
            getPatternsEditionPane().getPatternPropertyMenu().getItemDisjunct().fire();

        getListView().getSelectionModel().select(items.size()-1);
        select(patternGroups.size()-1);
    }


    @Override
    void removeEntry(int index) {
        boxItems.remove(index);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        getListView().setItems(items);

        for (int i = index+1; i < patternGroups.size(); i++) {
            patternGroups.get(i).decrementIndex();
        }

        patternGroups.remove(index);

        if (index == selectedIndex)
            select(0);

        for (int i = 0; i < boxItems.size(); i++) {
            boxItems.get(i).getChildren().remove(1);
            boxItems.get(i).add(new PatternCloseButton(getPatternsEditionPane(), i), 1, 0);
        }

        if (patternGroups.size() == 1) {
            getPatternsEditionPane().unselectAllMenus(getPatternsEditionPane().getPatternPropertyMenu().getItemDisjunct(), getPatternsEditionPane().getPatternPropertyMenu().getItemUndisjunct(), getPatternsEditionPane().getPatternPropertyMenu().getItemNNDisjunct());
        }
    }

    ArrayList<PatternGroup> getPatternGroups () {
        return patternGroups;
    }

    static ArrayList<GridPane> getBoxItems() {
        return boxItems;
    }
}
