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


class InteractionListBox extends VBox {
    private PatternsEditionPane patternsEditionPane;
    private ListView<GridPane> listView;
    private static ArrayList<GridPane> boxItems;
    private static ArrayList<InteractionItem> interactions = new ArrayList<>();
    private int selectedIndex;
    private Button addButton;

    public InteractionListBox(PatternsEditionPane patternsEditionPane) {
        super(5.0);
        Label titleLabel = new Label("Interactions");
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
                InteractionCloseButton button = (InteractionCloseButton) selection.getChildren().get(1);
                select(button.getIndex());
            }

            if (event.getClickCount() == 2) {
                Optional<InteractionItem> item;
                item = patternsEditionPane.getInteractionDialogBox(selectedIndex);
                item.ifPresent (value -> modifyEntry(value));
            }
        });
    }

    public Button buildAddButton() {
        addButton = new Button("Add");
        addButton.setDisable(true);
        addButton.setPrefWidth(125);
        addButton.setOnAction(e ->
        {
            Optional<InteractionItem> item = patternsEditionPane.getInteractionDialogBox(-1);
            item.ifPresent (value -> addEntry(value));
        });
        return addButton;
    }

    public Button buildModifyButton() {
        Button modifyButton = new Button("Modify");
        modifyButton.setPrefWidth(125);
        modifyButton.setOnAction(e ->
        {
            System.out.println("Modify button "+selectedIndex);
            Optional<InteractionItem> item;
            item = patternsEditionPane.getInteractionDialogBox(selectedIndex);
            item.ifPresent (value -> modifyEntry(value));
        });
        return modifyButton;
    }

    void select(int index) {
        System.out.println("Select "+index);
        selectedIndex = index;
    }

    void addEntry(InteractionItem item) {
        Label label = new Label(item.getLabel());
        System.out.println("Add Inter "+item.getInteraction().getLabel());
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        InteractionCloseButton button = new InteractionCloseButton(patternsEditionPane, interactions.size());

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        interactions.add (item);

        boxItems.add(pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        listView.getSelectionModel().select(items.size()-1);
        select(interactions.size()-1);
    }

    void modifyEntry(InteractionItem item) {
        Label label = new Label(item.getLabel());
        InteractionCloseButton button = new InteractionCloseButton(patternsEditionPane, selectedIndex);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(1));

        pane.add(label, 0, 0);
        label.setAlignment(Pos.BASELINE_CENTER);

        pane.add(button, 1, 0);
        button.setAlignment(Pos.BASELINE_RIGHT);

        interactions.set(selectedIndex,item);

        boxItems.set(selectedIndex,pane);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        System.out.println("Index "+selectedIndex);

        listView.getSelectionModel().select(selectedIndex);
        select(selectedIndex);
    }

    void removeEntry(int index) {
        boxItems.remove(index);
        ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
        listView.setItems(items);

        interactions.remove(index);

        select(0);

        for (int i = 0; i < boxItems.size(); i++) {
            boxItems.get(i).getChildren().remove(1);
            boxItems.get(i).add(new InteractionCloseButton(patternsEditionPane, i), 1, 0);
        }
    }


    void remove (PatternProperty property) {
        // we remove the interaction involving the given pattern property (if any)
        int i = 0;
        while (i < interactions.size()) {
            InteractionItem item = interactions.get(i);
            if ((item.getPatternProperty1() == property) || (item.getPatternProperty2() == property)) {
                patternsEditionPane.getInteractionListBox().removeEntry(i);
            } else {
                i++;
            }
        }
    }

    void updateLabel (String label, PatternProperty property) {
        for (int i = 0; i < interactions.size(); i++) {
            InteractionItem item = interactions.get(i);
            if ((item.getPatternProperty1() == property) || (item.getPatternProperty2() == property)) {
                ((Label) boxItems.get(i).getChildren().get(0)).setText(item.getLabel());
            }
        }
    }

    public static ArrayList<InteractionItem> getInteractions() {
        return interactions;
    }

    public Button getAddButton() {
        return addButton;
    }
}
